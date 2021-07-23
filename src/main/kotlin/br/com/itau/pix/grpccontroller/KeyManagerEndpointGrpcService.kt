package br.com.itau.pix.grpccontroller

import br.com.itau.pix.TipoDaConta
import br.com.itau.pix.clienthttp.ClienteBCB
import br.com.itau.pix.exception.AccountNotFoundException
import br.com.itau.pix.clienthttp.ConsultaCliente
import br.com.itau.pix.exception.KeyRegisterBCBException
import br.com.itau.pix.exception.KeyRegisteredException
import br.com.itau.pix.model.*
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Valid
import javax.validation.Validator

@Validated
@Singleton
class KeyManagerEndpointGrpcService(@Inject val keyPixRepository: KeyPixRepository, @Inject val consultaCliente: ConsultaCliente, @Inject val validator: Validator,
                                    @Inject val clienteBCB: ClienteBCB) {

    fun keyRegister(@Valid keyPixRequest: KeyPixRequest) : KeyPix {

        val keyPix = keyPixRequest.transformaParaKey(validator)

        val possivelClienteItauResponse = consultaCliente.consultaBanco(keyPix.clientId, keyPix.tipoConta.name)


        if (possivelClienteItauResponse.body.isEmpty){
          throw AccountNotFoundException("Cliente não encontrado!")
        }

        val clienteItau =  possivelClienteItauResponse.body()

        val bankAccount = Account(participant = clienteItau.instituicao.ispb, branch = clienteItau.agencia, accountNumber = clienteItau.numero,
            accountType = if (keyPix.tipoConta==TipoDaConta.CONTA_CORRENTE) TipoContaBCB.CACC else TipoContaBCB.SVGS )

        val owner = Cliente(type = OwnerType.NATURAL_PERSON, name = clienteItau.titular.nome, taxIdNumber = clienteItau.titular.cpf)

        val registerKeyBCBRequest = RegisterBCBRequest(keyType = keyPix.tipoChave, key = keyPix.valorChave, bankAccount = bankAccount, owner = owner)


        val  possivelChave  = keyPixRepository.findByClientIdAndValorChave(keyPix.clientId, keyPix.valorChave)

        if (possivelChave.isPresent){
            throw KeyRegisteredException("Chave já registrada!")
        }


        try {
            val registerKeyBCB = clienteBCB.keyRegisterBCB(registerKeyBCBRequest)
            keyPixRepository.save(keyPix)

            if(keyPix.tipoChave==TipoChave.RANDOM){
                keyPix.valorChave=registerKeyBCB.body().key
                keyPixRepository.update(keyPix)
            }

        }catch (e : HttpClientResponseException){
            throw KeyRegisterBCBException("Não foi possível registrar!")
            e.printStackTrace()
        }




        return keyPix

    }
}