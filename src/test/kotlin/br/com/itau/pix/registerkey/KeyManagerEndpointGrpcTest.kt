package br.com.itau.pix.registerkey

import br.com.itau.pix.*
import br.com.itau.pix.clienthttp.ClienteBCB
import br.com.itau.pix.clienthttp.ConsultaCliente
import br.com.itau.pix.model.*
import io.grpc.ManagedChannel
import io.grpc.Status

import io.grpc.StatusRuntimeException


import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean


import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.netty.handler.codec.http.HttpResponseStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import javax.inject.Inject


import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class KeyManagerEndpointGrpcTest(val keyPixRepository: KeyPixRepository, val grpcClient : KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub){

    @field:Inject
    lateinit var clienteBCB: ClienteBCB

    @field:Inject
    lateinit var clientContasItau: ConsultaCliente

    @BeforeEach
    fun Setup(){

        keyPixRepository.deleteAll()

        Mockito.reset(clienteBCB)

        //Mock Itaú
        val contaClientResponse = ClienteItauResponse(tipo = "CONTA_CORRENTE", instituicao = Instituicao("ITAÚ UNIBANCO S.A.","60701190"),
            agencia = "0001", numero = "125987", titular = Titular(id = "ae93a61c-0642-43b3-bb8e-a17072295955", nome = "Leonardo Silva", cpf = "407.644.420-58")
        )

        Mockito.`when`(clientContasItau.consultaBanco("ae93a61c-0642-43b3-bb8e-a17072295955","CONTA_CORRENTE")).thenReturn(
            HttpResponse.ok(contaClientResponse))


    }


    @Test
    fun `deve adicionar uma nova chave se o cliente existir no sistema do itau e o registro for feito com sucesso no BCB`(){

        //Cenário
        val bcbRegisterResponse = RegisterBCBResponse(keyType = TipoChave.CPF, key = "ae93a61c-0642-43b3-bb8e-a17072295955", bankAccount = Account(participant = "60701190", branch = "0001",
            accountNumber = "125987", accountType = TipoContaBCB.CACC), owner = Cliente(type = OwnerType.NATURAL_PERSON, name = "Leonardo Silva",
            taxIdNumber = "40764442058"),createdAt = LocalDateTime.now().toString())


        val bankAccount = Account(participant = "60701190", branch = "0001", accountNumber = "125987",
            accountType = TipoContaBCB.CACC )

        val owner = Cliente(type = OwnerType.NATURAL_PERSON, name = "Leonardo Silva", taxIdNumber = "407.644.420-58")

        val registerKeyBCBRequest = RegisterBCBRequest(keyType = TipoChave.CPF, key = "407.644.420-58", bankAccount = bankAccount, owner = owner)

        Mockito.`when`(clienteBCB.keyRegisterBCB(registerBCBRequest = registerKeyBCBRequest)).thenReturn(
            HttpResponse.created(bcbRegisterResponse))


        //Ação e validação
         assertDoesNotThrow {
             grpcClient.adicionar(
                KeyManagerGrpcRequest.newBuilder().setClienteId("ae93a61c-0642-43b3-bb8e-a17072295955")
                    .setTipoChave(TipoDeChave.valueOf("CPF")).setValorChave("407.644.420-58")
                    .setTipoConta(TipoDaConta.valueOf("CONTA_CORRENTE")).build()
            )

        }

        //Validação se foi salvo no banco
        assertTrue(keyPixRepository.existsById(1))



    }

    @Test
    fun `nao deve adicionar uma nova chave quando o registro no BCB nao for feito com sucesso`(){

        //Cenário

        val bankAccount = Account(participant = "60701190", branch = "0001", accountNumber = "125987",
            accountType = TipoContaBCB.CACC )

        val owner = Cliente(type = OwnerType.NATURAL_PERSON, name = "Leonardo Silva", taxIdNumber = "407.644.420-58")

        val registerKeyBCBRequest = RegisterBCBRequest(keyType = TipoChave.CPF, key = "407.644.420-58", bankAccount = bankAccount, owner = owner)

        Mockito.`when`(clienteBCB.keyRegisterBCB(registerBCBRequest = registerKeyBCBRequest)).thenThrow(HttpClientResponseException::class.java)


        //Ação
        val thrown = assertThrows<StatusRuntimeException> {
                 grpcClient.adicionar(
                KeyManagerGrpcRequest.newBuilder().setClienteId("ae93a61c-0642-43b3-bb8e-a17072295955")
                    .setTipoChave(TipoDeChave.valueOf("CPF")).setValorChave("407.644.420-58")
                    .setTipoConta(TipoDaConta.valueOf("CONTA_CORRENTE")).build()
            )

        }

        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Não foi possível salvar a chave!", status.description)
            //validação se não foi salvo no banco
            assertFalse(keyPixRepository.existsById(1))
        }


    }

    @Test
    fun `nao deve adicionar uma nova chave se os parametros extiverem nulos vazios`(){

        //Cenário

        //Ação

        val thrown = assertThrows<StatusRuntimeException>{
            grpcClient.adicionar(
                KeyManagerGrpcRequest.newBuilder().build())
        }

        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Preenchimento inválido!", status.description)
        }


    }


    @Test
    fun `nao deve adicionar uma nova chave se a mesma ja estiver cadastrada`(){

        //Cenário

        keyPixRepository.save(KeyPix(clientId = "ae93a61c-0642-43b3-bb8e-a17072295955", tipoChave = TipoChave.CPF, valorChave = "092.276.130-21", tipoConta = TipoDaConta.CONTA_CORRENTE))

        //Ação

        val thrown = assertThrows<StatusRuntimeException>{
            grpcClient.adicionar(
                KeyManagerGrpcRequest.newBuilder().setClienteId("ae93a61c-0642-43b3-bb8e-a17072295955")
                    .setTipoChave(TipoDeChave.valueOf("CPF")).setValorChave("092.276.130-21")
                    .setTipoConta(TipoDaConta.valueOf("CONTA_CORRENTE")).build())
        }

        with(thrown){
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Chave já registrada!", status.description)
        }


    }



    @Factory
    class Cliente{
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel) : KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub?{
            return KeyManagerGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

    @MockBean(ConsultaCliente::class)
    fun contasItauMock() : ConsultaCliente {
        return Mockito.mock(ConsultaCliente::class.java)
    }



    @MockBean(ClienteBCB::class)
    fun clienteBCBMock() : ClienteBCB {
        return Mockito.mock(ClienteBCB::class.java)
    }



}

