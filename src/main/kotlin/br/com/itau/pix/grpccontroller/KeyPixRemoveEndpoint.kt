package br.com.itau.pix.grpccontroller

import br.com.itau.pix.KeyPixRemoveRequest
import br.com.itau.pix.KeyPixRemoveResponse
import br.com.itau.pix.KeyPixRemoveServiceGrpc
import br.com.itau.pix.clienthttp.ClienteBCB
import br.com.itau.pix.exception.KeyPixNotFound
import br.com.itau.pix.exception.KeyRemoveBCBException
import br.com.itau.pix.handler.ErrorAroundHandler
import br.com.itau.pix.model.KeyPixRepository
import br.com.itau.pix.model.PixRemoveRequest
import br.com.itau.pix.model.RemoveBCBRequest
import br.com.itau.pix.transformarParaPixRemoveRequest
import io.grpc.stub.StreamObserver
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Validated
@Singleton
@ErrorAroundHandler
class KeyPixRemoveEndpoint(@Inject val keyPixRepository: KeyPixRepository, @Inject val clienteBCB: ClienteBCB) : KeyPixRemoveServiceGrpc.KeyPixRemoveServiceImplBase()  {

    override fun removekey(request: KeyPixRemoveRequest, responseObserver: StreamObserver<KeyPixRemoveResponse>) {
       val removeRequest = request.transformarParaPixRemoveRequest()

       val keyPixRemove = removeRequest(removeRequest)


       val  possivelPix = keyPixRepository.findByIdAndClientId(pixId = keyPixRemove.pixId.toLong(),  clientId = keyPixRemove.clienteId)

       if (possivelPix.isEmpty){
            throw KeyPixNotFound("Chave não encontrada!")
       }

       val removeBCBRequest = RemoveBCBRequest(key = possivelPix.get().valorChave, participant = "60701190")

        try {
            clienteBCB.keyRemoveBCB(removeBCBRequest)
            keyPixRepository.delete(possivelPix.get())
        }catch (e: HttpClientResponseException){
            e.printStackTrace()
            throw KeyRemoveBCBException("Falha ao tentar remover!")
        }


       responseObserver.onNext(KeyPixRemoveResponse.newBuilder().setMessage("Chave removida com sucesso!").build())
       responseObserver.onCompleted()


    }

}


fun removeRequest(@Valid keyPixRemoveRequest: PixRemoveRequest): PixRemoveRequest {

    return keyPixRemoveRequest

}

