package com.udemykotlin.pontointeligente.controller

import com.udemykotlin.pontointeligente.documents.Funcionario
import com.udemykotlin.pontointeligente.dtos.FuncionarioDto
import com.udemykotlin.pontointeligente.response.Response
import com.udemykotlin.pontointeligente.services.FuncionarioService
import com.udemykotlin.pontointeligente.utils.SenhaUtils
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/funcionarios")
class FuncionarioController(val  funcionarioService: FuncionarioService) {

    @PutMapping("/{id}")
    fun atualizar(@PathVariable("id") id: String, @Valid @RequestBody funcionarioDto: FuncionarioDto,
                  result: BindingResult): ResponseEntity<Response<FuncionarioDto>> {

        val response: Response<FuncionarioDto> = Response<FuncionarioDto>()
        val funcionario: Funcionario? = funcionarioService.buscarPorId(id)

        if(funcionario == null){
            result.addError(ObjectError("funcionario", "Funcionario não encontrado."))
        }

        if(result.hasErrors()){
            for(erro in result.allErrors) response.erros.add(erro.defaultMessage!!)
            return ResponseEntity.badRequest().body(response)
        }

        var funcAtualizar: Funcionario = atualizarDadosFuncionario(funcionario!!, funcionarioDto)
        funcAtualizar = funcionarioService.persistir(funcAtualizar)
        response.data = converterFuncionarioDto(funcAtualizar)

        return ResponseEntity.ok().body(response)
    }

    /**
     * Função privada responsável por atualizar dados do Funcionario.
     */
    private fun atualizarDadosFuncionario(funcionario: Funcionario, funcionarioDto: FuncionarioDto): Funcionario{
        var senha: String
        if(funcionarioDto.senha == null){
            senha = funcionario.senha
        }else {
            senha = SenhaUtils().gerarBcrypt(funcionarioDto.senha)
        }

        return Funcionario(funcionarioDto.nome, funcionario.email, senha,
                           funcionario.cpf, funcionario.perfil, funcionario.empresaId,
                           funcionarioDto.valorHora?.toDouble(), funcionarioDto.qtdHorasTrabalhoDia?.toFloat(),
                           funcionarioDto.qtdHorasAlmoco?.toFloat(), funcionario.id)
    }

    /**
     * Funcao privada responsável por converter Funcionaro para DTO.
     */
    private fun converterFuncionarioDto(funcionario: Funcionario): FuncionarioDto = FuncionarioDto(funcionario.nome, funcionario.email, "",
            funcionario.valorHora.toString(), funcionario.qtdHorasTrabalhoDia.toString(), funcionario.qtdHorasAlmoco.toString(), funcionario.id)
}