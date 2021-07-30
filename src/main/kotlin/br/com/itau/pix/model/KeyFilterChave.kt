package br.com.itau.pix.model

import br.com.itau.pix.TipoDaConta
import br.com.itau.pix.clienthttp.ClienteBCB
import br.com.itau.pix.exception.KeyPixNotFound
import io.micronaut.core.annotation.Introspected
import java.lang.Exception
import java.util.*
import javax.validation.constraints.NotBlank

@Introspected
class KeyFilterChave(@field:NotBlank val key : String) : KeyFilter {
    lateinit var details : DetailsKeyPix
    lateinit var keypix : RegisterBCBResponse
    override fun filter(keyPixRepository: KeyPixRepository, clienteBCB: ClienteBCB): DetailsKeyPix {

        val keyRequestBCB = clienteBCB.keyDetails(key = key)

        val bancos = Bancos()

        if (keyRequestBCB.body.isPresent){
            keypix = keyRequestBCB.body()
            details = DetailsKeyPix(
                tipoChave = keypix.keyType,
                valorChave = keypix.key,
                titular = keypix.owner.name,
                cpf = keypix.owner.taxIdNumber,
                banco = bancos.nome(keypix.bankAccount.participant),
                agencia = keypix.bankAccount.branch,
                numeroConta = keypix.bankAccount.accountNumber,
                tipoConta = if (keypix.bankAccount.accountType==TipoContaBCB.CACC) TipoConta.CONTA_CORRENTE else TipoConta.CONTA_POUPANCA,
                criadaEm = keypix.createdAt
            )

            return details

        }else{

            val keyRequest = keyPixRepository.findByValorChave(key = key)

            if (keyRequest.isPresent) {

                val key = keyRequest
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

                details.pixId = key.get().id.toString()
                details.clienteId = key.get().clientId

                return details
            }else{
                throw KeyPixNotFound("Chave não encontrada!")
            }
        }
    }

}