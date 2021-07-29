package br.com.itau.pix

import br.com.itau.pix.model.KeyDetailsRequest
import br.com.itau.pix.model.PixRemoveRequest
import br.com.itau.pix.model.KeyPixRequest
import br.com.itau.pix.model.TipoChave

fun KeyManagerGrpcRequest.transformarParaKeyPixRequest() : KeyPixRequest {
    return KeyPixRequest(
        clientId = clienteId,
        tipoChave = when(tipoChave){
            TipoDeChave.TIPO_CHAVE_DESCONHECIDO -> null
            else -> TipoChave.valueOf(tipoChave.name)
        },
        valorChave = valorChave,
        tipoConta = tipoConta

    )
}

fun KeyPixRemoveRequest.transformarParaPixRemoveRequest() : PixRemoveRequest{

    return PixRemoveRequest(pixId = pixId, clienteId = clienteId)

}
