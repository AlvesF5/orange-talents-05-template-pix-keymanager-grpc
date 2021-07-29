package br.com.itau.pix.registerkey

import br.com.itau.pix.ClienteItauResponse
import br.com.itau.pix.Instituicao
import br.com.itau.pix.Titular

import br.com.itau.pix.clienthttp.ConsultaCliente
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import javax.inject.Inject

@MicronautTest
class ClientContasItauTest {

        @field:Inject
        lateinit var clientContasItau: ConsultaCliente

        @Test
        fun `deve retornar verdadeiro se a conta do cliente existir`(){


            val contaClientResponse = ClienteItauResponse(tipo = "CONTA_CORRENTE", instituicao = Instituicao("ITAÚ UNIBANCO S.A.","60701190"),
                agencia = "0001", numero = "125987", titular = Titular(id = "ae93a61c-0642-43b3-bb8e-a17072295955", nome = "Leonardo Silva", cpf = "40764442058")
            )

            Mockito.`when`(clientContasItau.consultaBanco("ae93a61c-0642-43b3-bb8e-a17072295955","CONTA_CORRENTE")).thenReturn(
            HttpResponse.ok(contaClientResponse))

            val consulta = clientContasItau.consultaBanco("ae93a61c-0642-43b3-bb8e-a17072295955","CONTA_CORRENTE")


            Assertions.assertEquals(HttpStatus.OK, consulta.status)
            Assertions.assertTrue(consulta.body().titular.nome.equals("Leonardo Silva"))

        }




    @Test
    fun `deve retornar Not Found e corpo da resposta vazio se a conta do cliente não existir`(){

        //Cenário
        val contaClientResponse = null

        //Ação
        Mockito.`when`(clientContasItau.consultaBanco("ae93a61c-0642-43b3-bb8e-a17072295955","CONTA_SALARIO")).thenReturn(
            HttpResponse.notFound())


        val consulta = clientContasItau.consultaBanco("ae93a61c-0642-43b3-bb8e-a17072295955","CONTA_SALARIO")


       //Validação
        Assertions.assertEquals(HttpStatus.NOT_FOUND, consulta.status)
        Assertions.assertTrue(consulta.body()==contaClientResponse)

    }

        @MockBean(ConsultaCliente::class)
        fun contasItauMock() : ConsultaCliente {
            return Mockito.mock(ConsultaCliente::class.java)
        }


}
