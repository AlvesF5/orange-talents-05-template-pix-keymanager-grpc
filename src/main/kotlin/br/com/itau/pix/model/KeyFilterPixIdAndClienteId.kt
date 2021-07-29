package br.com.itau.pix.model

import br.com.itau.pix.clienthttp.ClienteBCB
import br.com.itau.pix.exception.KeyPixNotFound
import io.micronaut.core.annotation.Introspected
import java.lang.Exception
import java.util.*
import javax.validation.constraints.NotBlank

@Introspected
class KeyFilterPixIdAndClienteId(@field:NotBlank val pixId : String, @field:NotBlank val clienteId : String) : KeyFilter {

    lateinit var key : Optional<KeyPix>

    lateinit var details : DetailsKeyPix

    override fun filter(keyPixRepository: KeyPixRepository, clienteBCB: ClienteBCB) : DetailsKeyPix {
        val keyRequest = keyPixRepository.findByIdAndClientId(pixId = pixId.toLong(), clientId = clienteId)

        if (keyRequest.isPresent){
            key = keyRequest
            details = DetailsKeyPix(
                tipoChave = key.get().tipoChave,
                valorChave = key.get().valorChave,
                titular = key.get().accountItau!!.nomeTitular,
                cpf = key.get().accountItau!!.cpfTitular,
                banco = key.get().accountItau!!.nomeInstituicao,
                agencia = key.get().accountItau!!.agencia,
                numeroConta = key.get().accountItau!!.numero,
                tipoConta = TipoConta.valueOf(key.get().tipoConta.name),
                criadaEm = key.get().criadoEm.toString()
            )

            details.pixId=key.get().id.toString()
            details.clienteId=key.get().clientId

            return details

        }else{
            throw KeyPixNotFound("Chave não encontrada!")
        }


    }

}