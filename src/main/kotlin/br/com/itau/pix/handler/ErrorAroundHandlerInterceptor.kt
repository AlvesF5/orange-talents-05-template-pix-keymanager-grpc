package br.com.itau.pix.handler

import br.com.itau.pix.exception.AccountNotFoundException
import br.com.itau.pix.exception.KeyPixNotFound
import br.com.itau.pix.exception.KeyRegisteredException
import br.com.itau.pix.handler.ErrorAroundHandler
import io.grpc.Status

import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import io.micronaut.http.client.exceptions.HttpClientResponseException
import java.lang.IllegalArgumentException
import javax.inject.Singleton
import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException

@Singleton
@InterceptorBean(ErrorAroundHandler::class)
class ErrorAroundHandlerInterceptor : MethodInterceptor<Any, Any> {
    override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {

        try {
            return context.proceed()
        }catch (ex: Exception){

            val responseObserver = context.parameterValues[1] as StreamObserver<*>

            val status = when(ex){
                is ConstraintViolationException -> Status.INVALID_ARGUMENT.withCause(ex).withDescription("Preenchimento inválido!")

                is IllegalArgumentException -> Status.INVALID_ARGUMENT.withCause(ex).withDescription("Preenchimento inválido!")

                is HttpClientResponseException -> Status.INVALID_ARGUMENT.withCause(ex).withDescription("ID do cliente com formato inválido!")

                is KeyRegisteredException -> Status.ALREADY_EXISTS.withCause(ex).withDescription("Chave já registrada!")

                is AccountNotFoundException -> Status.NOT_FOUND.withCause(ex).withDescription("Cliente não encontrado!")

                is KeyPixNotFound -> Status.NOT_FOUND.withCause(ex).withDescription("Chave não encontrada!")

                else -> Status.UNKNOWN.withCause(ex).withDescription("Erro desconhecido")
            }

            responseObserver.onError(status.asRuntimeException())

        }

        return null
    }
}