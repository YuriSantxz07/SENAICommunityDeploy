package com.SenaiCommunity.BackEnd.DTO;

import com.SenaiCommunity.BackEnd.Entity.Usuario;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UsuarioSaidaDTO {

    private Long id;
    private String nome;
    private String email;
    private String tipoUsuario;
    private String urlFotoPerfil;
    private String bio;
    private LocalDate dataNascimento;
    private LocalDateTime dataCadastro;

    public UsuarioSaidaDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.tipoUsuario = usuario.getTipoUsuario();
        this.bio = usuario.getBio();
        this.dataNascimento = usuario.getDataNascimento();
        this.dataCadastro = usuario.getDataCadastro();

        String urlFoto = usuario.getFotoPerfil();

        if (urlFoto != null && urlFoto.startsWith("http")) {
            // 1. Se for uma URL completa (do Cloudinary), usa ela diretamente.
            this.urlFotoPerfil = urlFoto;
        } else if (urlFoto != null && !urlFoto.isBlank()) {
            // 2. Se for um nome de arquivo antigo (do sistema local), mantém a rota antiga.
            this.urlFotoPerfil = "/api/arquivos/" + urlFoto;
        } else {
            // 3. Se for nulo ou vazio, usa a imagem padrão correta.
            this.urlFotoPerfil = "/images/default-avatar.jpg"; //
        }
    }
}