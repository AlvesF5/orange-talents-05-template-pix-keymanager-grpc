package br.com.itau.pix.removeykey

import br.com.itau.pix.*
import br.com.itau.pix.clienthttp.ClienteBCB
import br.com.itau.pix.model.KeyPix
import br.com.itau.pix.model.KeyPixRepository
import br.com.itau.pix.model.RemoveBCBRequest
import br.com.itau.pix.model.TipoChave
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class KeyPixRemoveEndpointTest(val keyPixRepository: KeyPixRepository, val grpcClient : KeyPixRemoveServiceGrpc.KeyPixRemoveServiceBlockingStub){


    @field:Inject
    lateinit var clienteBCB: ClienteBCB

    @BeforeEach
    fun setup(){
        keyPixRepository.deleteAll()

        keyPixRepository.save(KeyPix(clientId = "ae93a61c-0642-43b3-bb8e-a17072295955", tipoChave = TipoChave.CPF, valorChave = "486.114.030-70", tipoConta = TipoDaConta.CONTA_CORRENTE))
    }


    @Test
    fun `nao deve remover a chave se a remocao no BCB falhar`(){

        //Cenário

        val removeRequest = RemoveBCBRequest(key = "486.114.030-70",participant = "60701190")
        Mockito.`when`(clienteBCB.keyRemoveBCB(removeRequest)).thenThrow(HttpClientResponseException::class.java)

        //Ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.removekey(
                KeyPixRemoveRequest.newBuilder().setPixId("1").setClienteId("ae93a61c-0642-43b3-bb8e-a17072295955").build()
            )
        }

        //Validação
        with(thrown){
            assertEquals(Status.UNKNOWN.code, status.code)
            assertEquals("Erro desconhecido!", status.description)
        }

    }


    @Test
    fun `deve remover a chave se a remocao no BCB for bem sucedida`(){

        //Cenário

        val removeRequest = RemoveBCBRequest(key = "486.114.030-70",participant = "60701190")
        Mockito.`when`(clienteBCB.keyRemoveBCB(removeRequest)).thenReturn(HttpResponse.ok())

        //Ação
         assertDoesNotThrow {
            grpcClient.removekey(
                KeyPixRemoveRequest.newBuilder().setPixId("1").setClienteId("ae93a61c-0642-43b3-bb8e-a17072295955").build()
            )
        }

        //Validação se foi removida do banco
        assertTrue(keyPixRepository.findByClientIdAndValorChave(clientId = "ae93a61c-0642-43b3-bb8e-a17072295955", valorChave = "486.114.030-70" ).isEmpty)


    }

    @Test
    fun `nao deve remover a chave se o id da chave ou o id do cliente id nao forem compativeis com o registro da chave no banco`(){

        //Cenário //Cenário -> Setup é chamado


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

        //Cenário -> Setup é chamado


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
    fun `deve remover a chave se o id da chave E o id do cliente id  forem compativeis com o registro da chave no banco`(){

        //Cenário -> Setup é chamado


        //Ação e validação
        assertDoesNotThrow {
            grpcClient.removekey(
                KeyPixRemoveRequest.newBuilder().setPixId("1").setClienteId("ae93a61c-0642-43b3-bb8e-a17072295955").build()
            )
        }
       //Validação se foi removido do banco
        assertTrue(keyPixRepository.findByIdAndClientId(pixId = 1, clientId = "ae93a61c-0642-43b3-bb8e-a17072295955").isEmpty)


    }


    @Factory
    class Cliente{
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel) : KeyPixRemoveServiceGrpc.KeyPixRemoveServiceBlockingStub{
            return KeyPixRemoveServiceGrpc.newBlockingStub(channel)
        }
    }

    @MockBean(ClienteBCB::class)
    fun clienteBCBMock() : ClienteBCB {
        return Mockito.mock(ClienteBCB::class.java)
    }


}