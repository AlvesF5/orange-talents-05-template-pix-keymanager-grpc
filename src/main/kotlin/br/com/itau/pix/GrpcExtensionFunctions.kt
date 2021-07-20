package br.com.itau.pix

import br.com.itau.pix.model.KeyPixRequest

fun KeyManagerGrpcRequest.transformarParaKeyPixRequest() : KeyPixRequest {
    return KeyPixRequest(
        clientId = clienteId,
        tipoChave = tipoChave,
        valorChave = valorChave,
        tipoConta = tipoConta

    )
}
