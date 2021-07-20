package br.com.itau.pix.model





import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator


enum class TipoChave {
    TIPO_CHAVE_DESCONHECIDO {
        override fun valida(valorChave: String?): Boolean {
            return false
        }
    },
    CPF {
        override fun valida(valorChave: String?): Boolean {
            if (valorChave.isNullOrBlank()){
                return false
            }

            if (valorChave.matches("[0-9]+".toRegex())){
                return false
            }

            return CPFValidator().run {
                initialize(null)
                isValid(valorChave, null)
            }

        }
    }, CELULAR {
        override fun valida(valorChave: String?): Boolean {
            if (valorChave.isNullOrBlank()){
                return false
            }
            return valorChave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    }, EMAIL {
        override fun valida(valorChave: String?): Boolean {
           if (valorChave.isNullOrBlank()){
               return false
           }
           return EmailValidator().run {
               initialize(null)
               isValid(valorChave,null)
           }
        }
    }, ALEATORIA {
        override fun valida(valorChave: String?) = valorChave.isNullOrBlank()
    };


    abstract fun valida(valorChave: String?) : Boolean
}