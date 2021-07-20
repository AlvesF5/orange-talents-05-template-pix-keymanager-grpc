package br.com.itau.pix

import br.com.itau.pix.model.KeyPix
import br.com.itau.pix.model.KeyPixRepository
import br.com.itau.pix.model.KeyPixRequest
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Valid
import javax.validation.Validator

@Validated
@Singleton
class KeyManagerEndpointGrpcService(@Inject val keyPixRepository: KeyPixRepository, @Inject val consultaCliente:  ConsultaCliente, val validator: Validator) {

    fun keyRegister(@Valid keyPixRequest: KeyPixRequest) : KeyPix {

        val keyPix = keyPixRequest.transformaParaKey(validator)

        val possivelClienteItauResponse = consultaCliente.consultaBanco(keyPix.clientId, keyPix.tipoConta.name)

        if (possivelClienteItauResponse.body.isEmpty){
          throw AccountNotFoundException("Cliente não encontrado!")
        }


        val  possivelChave  = keyPixRepository.findByClientIdAndValorChave(keyPix!!.clientId,keyPix!!.valorChave)

        if (possivelChave.isPresent){
            throw KeyRegisteredException("Chave já registrada!")
        }

        keyPixRepository.save(keyPix)

        return keyPix

    }
}