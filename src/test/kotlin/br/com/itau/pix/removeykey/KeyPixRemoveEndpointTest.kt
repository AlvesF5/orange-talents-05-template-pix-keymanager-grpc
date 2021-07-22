package br.com.itau.pix.removeykey

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
internal class KeyPixRemoveEndpointTest(val keyPixRepository: KeyPixRepository, val grpcClient : KeyPixRemoveServiceGrpc.KeyPixRemoveServiceBlockingStub){

    @BeforeEach
    fun setup(){
        keyPixRepository.deleteAll()
    }

    @Test
    fun `nao deve remover a chave se o id da chave OU o id do cliente id nao forem compativeis com o registro da chave no banco`(){

        //Cenário
        keyPixRepository.save(KeyPix(clientId = "ae93a61c-0642-43b3-bb8e-a17072295955", tipoChave = TipoChave.CPF, valorChave = "486.114.030-70", tipoConta = TipoDaConta.CONTA_CORRENTE))


        //Ação

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.removekey(
                KeyPixRemoveRequest.newBuilder().setPixId("1").setClienteId("be93a61c-0642-43b3-bb8e-a17072295200").build()
            )
        }

        //Validação
        with(thrown){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave não encontrada!", status.description)
        }


    }

    @Test
    fun `nao deve remover a chave se o id da chave E o id do cliente id nao forem compativeis com o registro da chave no banco`(){

        //Cenário
        keyPixRepository.save(KeyPix(clientId = "ae93a61c-0642-43b3-bb8e-a17072295955", tipoChave = TipoChave.CPF, valorChave = "486.114.030-70", tipoConta = TipoDaConta.CONTA_CORRENTE))


        //Ação

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.removekey(
                KeyPixRemoveRequest.newBuilder().setPixId("2").setClienteId("be93a61c-0642-43b3-bb8e-a17072295200").build()
            )
        }

        //Validação
        with(thrown){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave não encontrada!", status.description)
        }


    }


    @Test
    fun `nao deve remover a chave se o id da chave E o id do cliente nao forem preenchidos`(){

        //Cenário
        keyPixRepository.save(KeyPix(clientId = "ae93a61c-0642-43b3-bb8e-a17072295955", tipoChave = TipoChave.CPF, valorChave = "486.114.030-70", tipoConta = TipoDaConta.CONTA_CORRENTE))


        //Ação

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.removekey(
                KeyPixRemoveRequest.newBuilder().build()
            )
        }

        //Validação
        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Preenchimento inválido!", status.description)
        }


    }



    @Factory
    class Cliente{
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel) : KeyPixRemoveServiceGrpc.KeyPixRemoveServiceBlockingStub{
            return KeyPixRemoveServiceGrpc.newBlockingStub(channel)
        }
    }


}