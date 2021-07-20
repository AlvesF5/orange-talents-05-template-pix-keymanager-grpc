package br.com.itau.pix


import br.com.itau.pix.model.KeyPixRequest
import javax.inject.Singleton
import kotlin.reflect.KClass



import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload


@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidKeyPixValidator::class])
annotation class ValidKeyPix(
    val message: String = "Chave inválida!",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

@Singleton
class ValidKeyPixValidator: ConstraintValidator<ValidKeyPix, KeyPixRequest>{

    override fun isValid(value: KeyPixRequest?, context: ConstraintValidatorContext): Boolean {

        if (value?.tipoChave == null){
            return false
        }

        return value.tipoChave.valida(value.valorChave)
    }




}
