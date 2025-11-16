package com.SenaiCommunity.BackEnd.Service;

import com.SenaiCommunity.BackEnd.DTO.AlunoEntradaDTO;
import com.SenaiCommunity.BackEnd.DTO.AlunoSaidaDTO;
import com.SenaiCommunity.BackEnd.Entity.Aluno;
import com.SenaiCommunity.BackEnd.Entity.Projeto;
import com.SenaiCommunity.BackEnd.Entity.Role;
import com.SenaiCommunity.BackEnd.Repository.AlunoRepository;
import com.SenaiCommunity.BackEnd.Repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AlunoService {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ArquivoMidiaService midiaService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Métodos de conversão direto no service:

    private Aluno toEntity(AlunoEntradaDTO dto) {
        Aluno aluno = new Aluno();
        aluno.setNome(dto.getNome());
        aluno.setEmail(dto.getEmail());
        aluno.setSenha(passwordEncoder.encode(dto.getSenha()));
        aluno.setFotoPerfil(dto.getFotoPerfil());
        aluno.setCurso(dto.getCurso());
        aluno.setPeriodo(dto.getPeriodo());
        aluno.setDataNascimento(dto.getDataNascimento());
        return aluno;
    }

    private AlunoSaidaDTO toDTO(Aluno aluno) {
        AlunoSaidaDTO dto = new AlunoSaidaDTO();
        dto.setId(aluno.getId());
        dto.setNome(aluno.getNome());
        dto.setEmail(aluno.getEmail());
        dto.setCurso(aluno.getCurso());
        dto.setPeriodo(aluno.getPeriodo());
        dto.setDataCadastro(aluno.getDataCadastro());
        dto.setBio(aluno.getBio());
        dto.setDataNascimento(aluno.getDataNascimento());

        String urlFoto = aluno.getFotoPerfil();

        if (urlFoto != null && urlFoto.startsWith("http")) {
            // 1. Se for uma URL completa (do Cloudinary), usa ela diretamente.
            dto.setFotoPerfil(urlFoto);
        } else if (urlFoto != null && !urlFoto.isBlank()) {
            // 2. Se for um nome de arquivo antigo (do sistema local), mantém a rota antiga.
            dto.setFotoPerfil("/api/arquivos/" + urlFoto);
        } else {
            // 3. Se for nulo ou vazio, usa a imagem padrão correta.
            dto.setFotoPerfil("/images/default-avatar.jpg"); //
        }

        dto.setProjetos(
                aluno.getProjetos() != null
                        ? aluno.getProjetos().stream().map(Projeto::getId).collect(Collectors.toList())
                        : new ArrayList<>()
        );

        return dto;
    }

    public AlunoSaidaDTO criarAlunoComFoto(AlunoEntradaDTO dto, MultipartFile foto) {
        Aluno aluno = toEntity(dto);
        aluno.setDataCadastro(LocalDateTime.now());
        aluno.setTipoUsuario("ALUNO");

        Role roleAluno = roleRepository.findByNome("ALUNO")
                .orElseThrow(() -> new RuntimeException("Role ALUNO não encontrada"));
        aluno.setRoles(Set.of(roleAluno));

        if (foto != null && !foto.isEmpty()) {
            try {
                // A chamada para salvarFoto no cloudinary
                String fileName = midiaService.upload(foto);
                aluno.setFotoPerfil(fileName);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao salvar a foto do aluno", e);
            }
        } else {
            aluno.setFotoPerfil(null);
        }

        Aluno salvo = alunoRepository.save(aluno);
        return toDTO(salvo);
    }

//    private String salvarFoto(MultipartFile foto) throws IOException {
//        String nomeArquivo = System.currentTimeMillis() + "_" + StringUtils.cleanPath(foto.getOriginalFilename());
//        Path diretorioDeUpload = Paths.get(uploadDir);
//
//        // Garante que o diretório de uploads exista
//        if (Files.notExists(diretorioDeUpload)) {
//            Files.createDirectories(diretorioDeUpload);
//        }
//
//        Path caminhoDoArquivo = diretorioDeUpload.resolve(nomeArquivo);
//        foto.transferTo(caminhoDoArquivo);
//        return nomeArquivo;
//    }


    public List<AlunoSaidaDTO> listarTodos() {
        return alunoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AlunoSaidaDTO buscarPorId(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));
        return toDTO(aluno);
    }

    public AlunoSaidaDTO atualizarAluno(Long id, AlunoEntradaDTO dto) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado"));

        aluno.setNome(dto.getNome());
        aluno.setEmail(dto.getEmail());
        aluno.setSenha(passwordEncoder.encode(dto.getSenha()));
        aluno.setFotoPerfil(dto.getFotoPerfil());
        aluno.setCurso(dto.getCurso());
        aluno.setPeriodo(dto.getPeriodo());

        Aluno atualizado = alunoRepository.save(aluno);
        return toDTO(atualizado);
    }

    public void deletarAluno(Long id) {
        if (!alunoRepository.existsById(id)) {
            throw new EntityNotFoundException("Aluno não encontrado");
        }
        alunoRepository.deleteById(id);
    }
}
