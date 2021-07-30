package br.com.itau.pix.detailskey

import br.com.itau.pix.*
import br.com.itau.pix.clienthttp.ClienteBCB
import br.com.itau.pix.model.*
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
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
internal class KeyPixDetailsEndpointTest(val keyPixRepository: KeyPixRepository,  val grpcClient : KeyPixDetailsServiceGrpc.KeyPixDetailsServiceBlockingStub){

    @field:Inject
    lateinit var clienteBCB: ClienteBCB

    @BeforeEach
    fun setup(){

        keyPixRepository.deleteAll()

    }


    @Test
    fun `nao deve retornar os detalhes de uma chave se os valores passados forem nulos ou em branco`(){

        //Cenário
        val key = KeyPix(clientId = "bc35591d-b547-4151-a325-4a9d2cd19614", TipoChave.valueOf(TipoChave.CPF.name),
            valorChave = "643.707.520-19", tipoConta = TipoDaConta.valueOf(TipoConta.CONTA_CORRENTE.name))

        keyPixRepository.save(key)


        //Ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.keydetails(KeyPixDetailsRequest.newBuilder().build())
        }

        //Validação
        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Preenchimento inválido!", status.description)
        }


    }

    @Test
    fun `deve retornar not found se a chave não for encotrada no banco local e no BCB`(){

        //Cenário
        val key = KeyPix(clientId = "bc35591d-b547-4151-a325-4a9d2cd19614", TipoChave.valueOf(TipoChave.CPF.name),
            valorChave = "643.707.520-19", tipoConta = TipoDaConta.valueOf(TipoConta.CONTA_CORRENTE.name))

        keyPixRepository.save(key)

        //Mock BCB
        Mockito.`when`(clienteBCB.keyDetails("377.352.460-99")).thenReturn(
            HttpResponse.notFound())


        //Ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.keydetails(KeyPixDetailsRequest.newBuilder().setKey("377.352.460-99").build())
        }

        //Validação

        with(thrown){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave não encontrada!", status.description)
        }

    }

    


    @MockBean(ClienteBCB::class)
    fun clienteBCBMock() : ClienteBCB {
        return Mockito.mock(ClienteBCB::class.java)
    }

    @Factory
    class Cliente{
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel) : KeyPixDetailsServiceGrpc.KeyPixDetailsServiceBlockingStub?{
            return KeyPixDetailsServiceGrpc.newBlockingStub(channel)
        }
    }

}

