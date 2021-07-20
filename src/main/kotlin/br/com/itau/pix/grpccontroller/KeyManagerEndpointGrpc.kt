package br.com.itau.pix.grpccontroller
import br.com.itau.pix.*
import br.com.itau.pix.handler.ErrorAroundHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
@ErrorAroundHandler
class KeyManagerEndpointGrpc(@Inject val keyManagerEndpointGrpcService: KeyManagerEndpointGrpcService) :
    KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceImplBase() {

    override fun adicionar(request: KeyManagerGrpcRequest, responseObserver: StreamObserver<KeyManagerGrpcResponse>) {


    val keyRequest = request.transformarParaKeyPixRequest()

    val keyPix = keyManagerEndpointGrpcService.keyRegister(keyRequest)


    responseObserver.onNext(KeyManagerGrpcResponse.newBuilder().setId(keyPix.id.toString()).setClienteId(keyPix.clientId).build())
    responseObserver.onCompleted()

    }


}