package br.com.itau.pix.registerkey

import br.com.itau.pix.model.TipoChave
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested

internal class TipoChaveTest {

    @Nested
    inner class RANDOM {

        @Test
        fun `deve retornar verdadeiro quando o valor da chave aletoria estiver nulo ou vazio`(){
            with(TipoChave.RANDOM){
                assertTrue(valida(valorChave = null))
                assertTrue(valida(valorChave = ""))
            }
        }

        @Test
        fun `deve retornar falso quando o valor da chave aletoria estiver preenchido`(){
            with(TipoChave.RANDOM){
                assertFalse(valida(valorChave = "dfsdf-dfsdf-sdsa"))
            }
        }

    }

    @Nested
    inner class CPF{

        @Test
        fun `deve retornar falso se o valor da chave for nulo ou vazio`(){
            with(TipoChave.CPF){
                assertFalse(valida(valorChave = null))
                assertFalse(valida(valorChave = ""))
            }
        }

        @Test
        fun `deve retornar falso se o valor da chave não tiver formato de cpf válido`(){
            with(TipoChave.CPF){
                assertFalse(valida(valorChave = "025485023141"))
            }
        }

        @Test
        fun `deve retornar verdadeiro se o valor da chave tiver formato de cpf válido`(){
            with(TipoChave.CPF){
                assertTrue(valida(valorChave = "092.276.130-21"))
            }
        }

    }


    @Nested
    inner class PHONE {

        @Test
        fun `deve retornar falso se o valor da chave for nulo ou vazio`(){
            with(TipoChave.PHONE){
                assertFalse(valida(valorChave = null))
                assertFalse(valida(valorChave = ""))
            }
        }

        @Test
        fun `deve retornar falso se o valor da chave não tiver um formato de numero de celular valido`(){
            with(TipoChave.PHONE){
                assertFalse(valida(valorChave = "845605"))
            }
        }

        @Test
        fun `deve retornar verdadeiro se o valor da chave tiver um formato de numero de celular valido`(){
            with(TipoChave.PHONE){
                assertTrue(valida(valorChave = "+5573999307376"))
            }

        }

    }

    @Nested
    inner class EMAIL {

        @Test
        fun `deve retornar falso se o valor da chave estiver nulo ou vazio`(){
            with(TipoChave.EMAIL){
                assertFalse(valida(valorChave = null))
                assertFalse(valida(valorChave = ""))
            }
        }

        @Test
        fun `deve retornar falso se o valor da chave não for no formato de um email valido`(){
            with(TipoChave.EMAIL){
                assertFalse(valida(valorChave = "matheus.alvesemail"))
            }

        }

        @Test
        fun `deve retornar verdadeiro de o valor da chave dor no formato de um email valido`(){
            with(TipoChave.EMAIL){
                assertTrue(valida(valorChave = "matheus.alves@zup.com.br"))
            }
        }

    }

}