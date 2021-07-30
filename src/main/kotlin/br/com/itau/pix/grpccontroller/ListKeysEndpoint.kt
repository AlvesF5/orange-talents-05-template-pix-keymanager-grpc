package br.com.itau.pix.grpccontroller

import br.com.itau.pix.ListKeysRequest
import br.com.itau.pix.ListKeysResponse
import br.com.itau.pix.ListKeysServiceGrpc
import br.com.itau.pix.handler.ErrorAroundHandler
import br.com.itau.pix.model.KeyPixRepository
import io.grpc.stub.StreamObserver
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorAroundHandler
class ListKeysEndpoint(@Inject val keyPixRepository: KeyPixRepository) : ListKeysServiceGrpc.ListKeysServiceImplBase() {

    override fun listKeys(request: ListKeysRequest, responseObserver: StreamObserver<ListKeysResponse>) {

        if (request.clienteId.isNullOrBlank()){
            throw IllegalArgumentException("O ClientId não pode ser nulo ou vazio")
        }

        val listKeys = keyPixRepository.findAllByClientId(request.clienteId).map { key ->
            ListKeysResponse.Keys.newBuilder()
                .setPixId(key.id.toString())
                .setTipoChave(key.tipoChave.toString())
                .setValorChave(key.valorChave)
                .setTipoConta(key.tipoConta.toString())
                .setCriadaEm(key.criadoEm.toString())
                .build()
        }



      responseObserver.onNext(ListKeysResponse.newBuilder().setClienteId(request.clienteId).addAllKeys(listKeys).build())
      responseObserver.onCompleted()


    }

}