package br.com.itau.pix.registerkey

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
internal class KeyManagerEndpointGrpcTest(val keyPixRepository: KeyPixRepository, val grpcClient : KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub){

    @BeforeEach
    fun Setup(){
        keyPixRepository.deleteAll()
    }


    @Test
    fun `deve adicionar uma nova chave`(){

        //Cenário


        //Ação
        val  response = grpcClient.adicionar(
            KeyManagerGrpcRequest.newBuilder().setClienteId("ae93a61c-0642-43b3-bb8e-a17072295955")
            .setTipoChave(TipoDeChave.valueOf("CPF")).setValorChave("092.276.130-21")
            .setTipoConta(TipoDaConta.valueOf("CONTA_CORRENTE")).build())

        //Validação
        assertNotNull(response.id)
        assertTrue(keyPixRepository.existsById(response.id.toLong()))

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



}

