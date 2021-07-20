package br.com.itau.pix

import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import javax.inject.Singleton
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
                is ConstraintViolationException -> Status.INVALID_ARGUMENT.withCause(ex).withDescription("Preenchimento iválido!")

                is KeyRegisteredException -> Status.ALREADY_EXISTS.withCause(ex).withDescription("Chave já registrada!")

                is AccountNotFoundException -> Status.NOT_FOUND.withCause(ex).withDescription("Cliente não encontrado!")

                else -> Status.UNKNOWN.withCause(ex).withDescription("Erro desconhecido")
            }

            responseObserver.onError(status.asRuntimeException())

        }

        return null
    }
}