package br.com.itau.pix.listkeys

import br.com.itau.pix.*
import br.com.itau.pix.model.KeyPix
import br.com.itau.pix.model.KeyPixRepository
import br.com.itau.pix.model.TipoChave
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class ListKeysEndpointTest(val keyPixRepository: KeyPixRepository, val grpcClient : ListKeysServiceGrpc.ListKeysServiceBlockingStub){

    @BeforeEach
    fun setup(){
        keyPixRepository.deleteAll()
    }


    @Test
    fun `deve retornar illegalargument se o clientId estivar nulo ou vazio`(){

        //Cenário

        keyPixRepository.save(KeyPix(clientId = "ae93a61c-0642-43b3-bb8e-a17072295955", tipoChave = TipoChave.CPF, valorChave = "092.276.130-21", tipoConta = TipoDaConta.CONTA_CORRENTE))

        //Ação

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.listKeys(ListKeysRequest.newBuilder().build())
        }

        //Validação
        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Preenchimento inválido!", status.description)
        }

    }


    @Test
    fun `deve retornar verdadeiro se a quantidade de chaves do cliente for igual a 1`(){

        //Cenário

        keyPixRepository.save(KeyPix(clientId = "ae93a61c-0642-43b3-bb8e-a17072295955", tipoChave = TipoChave.CPF, valorChave = "092.276.130-21", tipoConta = TipoDaConta.CONTA_CORRENTE))

        //Ação
        val quantidade =  grpcClient.listKeys(ListKeysRequest.newBuilder().setClienteId("ae93a61c-0642-43b3-bb8e-a17072295955").build()).keysCount

        //Validação
        assertTrue(quantidade==1)


    }

    @Test
    fun `deve retornar verdadeiro se a quantidade de chaves do cliente for igual a 0`(){

        //Cenário

        keyPixRepository.save(KeyPix(clientId = "ae93a61c-0642-43b3-bb8e-a17072295955", tipoChave = TipoChave.CPF, valorChave = "092.276.130-21", tipoConta = TipoDaConta.CONTA_CORRENTE))

        //Ação
        val quantidade =  grpcClient.listKeys(ListKeysRequest.newBuilder().setClienteId("by12a61c-0642-43b3-bb8e-a17072295720").build()).keysCount

        //Validação
        assertTrue(quantidade==0)


    }






    @Factory
    class Cliente{
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel) : ListKeysServiceGrpc.ListKeysServiceBlockingStub{
            return ListKeysServiceGrpc.newBlockingStub(channel)
        }
    }

}