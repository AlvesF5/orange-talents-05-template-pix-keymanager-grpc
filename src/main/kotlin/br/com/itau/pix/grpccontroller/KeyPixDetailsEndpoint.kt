package br.com.itau.pix.grpccontroller

import br.com.itau.pix.KeyPixDetailsRequest
import br.com.itau.pix.KeyPixDetailsResponse
import br.com.itau.pix.KeyPixDetailsServiceGrpc
import br.com.itau.pix.clienthttp.ClienteBCB
import br.com.itau.pix.handler.ErrorAroundHandler
import br.com.itau.pix.model.*
import io.grpc.stub.StreamObserver
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Valid

@Singleton
@ErrorAroundHandler
@Validated
class KeyPixDetailsEndpoint(@Inject val keyPixRepository: KeyPixRepository, @Inject val clienteBCB: ClienteBCB) : KeyPixDetailsServiceGrpc.KeyPixDetailsServiceImplBase() {

    var keyPix: Optional<KeyPix>? = null

    override fun keydetails(request: KeyPixDetailsRequest, responseObserver: StreamObserver<KeyPixDetailsResponse>) {

        val escolha = request.selectFilter()

        validEscolha(escolha)

        val keyDetails = escolha.filter(keyPixRepository, clienteBCB)


        responseObserver.onNext(KeyPixDetailsResponse.newBuilder().setPixId(keyDetails.pixId).setClienteId(keyDetails.clienteId)
            .setTipoDechave(keyDetails.tipoChave.name).setValorChave(keyDetails.valorChave).setTitular(keyDetails.titular)
            .setDocumentoTitular(keyDetails.cpf).setBanco(keyDetails.banco).setAgencia(keyDetails.agencia)
            .setNumeroConta(keyDetails.numeroConta).setTipoConta(keyDetails.tipoConta.name).setCriandaEm(keyDetails.criadaEm).build())
        responseObserver.onCompleted()

    }

    fun KeyPixDetailsRequest.selectFilter() : KeyFilter{
        val escolha = when(keyPixDetailsQueryCase){
            KeyPixDetailsRequest.KeyPixDetailsQueryCase.KEY -> KeyFilterChave(key = key)
            KeyPixDetailsRequest.KeyPixDetailsQueryCase.INTERNALQUERY -> KeyFilterPixIdAndClienteId(pixId = internalQuery.pixId, clienteId = internalQuery.clienteId)
            KeyPixDetailsRequest.KeyPixDetailsQueryCase.KEYPIXDETAILSQUERY_NOT_SET -> KeyFilterInvalid()
        }

        return escolha
    }


    fun validEscolha(@Valid keyFilter: KeyFilter): KeyFilter {
        return keyFilter
    }




}