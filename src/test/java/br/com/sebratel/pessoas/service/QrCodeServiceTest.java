package br.com.sebratel.pessoas.service;

import br.com.sebratel.pessoas.dto.QrCodeInputDTO;
import br.com.sebratel.pessoas.dto.QrCodeOutputDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class QrCodeServiceTest {

    @TempDir
    Path dir;

    /** Par de chaves gerado no teste: nenhuma chave real entra no repositorio. */
    private QrCodeService serviceWithGeneratedKeys() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();
        Path pub = dir.resolve("pub.pem");
        Path priv = dir.resolve("priv.pem");
        Files.writeString(pub, "-----BEGIN PUBLIC KEY-----\n" + Base64.getMimeEncoder().encodeToString(pair.getPublic().getEncoded())
                + "\n-----END PUBLIC KEY-----\n");
        Files.writeString(priv, "-----BEGIN PRIVATE KEY-----\n" + Base64.getMimeEncoder().encodeToString(pair.getPrivate().getEncoded())
                + "\n-----END PRIVATE KEY-----\n");
        return new QrCodeService(pub.toUri().toString(), priv.toUri().toString());
    }

    @Test
    void gerarEDecryptarShouldWorkTogether() throws Exception {
        QrCodeService service = serviceWithGeneratedKeys();
        String originalJson = "{\"id\":1, \"name\":\"John Doe\"}";
        QrCodeInputDTO input = new QrCodeInputDTO();
        input.setJson(originalJson);

        // Act - Generate
        QrCodeOutputDTO output = service.gerarQrCodeParaFuncionario(input);

        // Assert - Generate
        assertNotNull(output);
        assertNotNull(output.getEncryptedData());
        assertTrue(output.getQrCodeImage().startsWith("data:image/png;base64,"));

        // Act - Decrypt
        String decryptedJson = service.decryptarQrCode(output.getEncryptedData());

        // Assert - Decrypt
        assertEquals(originalJson, decryptedJson);
    }

    @Test
    void decryptarQrCodeShouldThrowExceptionWhenDataIsInvalid() throws Exception {
        QrCodeService service = serviceWithGeneratedKeys();
        String invalidData = "not-base64-and-not-encrypted";

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.decryptarQrCode(invalidData));
    }

    @Test
    void gerarQrCodeShouldThrowExceptionWhenPublicKeyNotFound() {
        QrCodeService serviceWithMissingKey = new QrCodeService("non_existent.pem", "id_rsa_private.pem");
        QrCodeInputDTO input = new QrCodeInputDTO();
        input.setJson("{}");

        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> serviceWithMissingKey.gerarQrCodeParaFuncionario(input));
        assertEquals("Chave publica nao encontrada", exception.getMessage());
    }

    @Test
    void decryptarQrCodeShouldThrowExceptionWhenPrivateKeyNotFound() {
        QrCodeService serviceWithMissingKey = new QrCodeService("id_rsa_public.pem", "non_existent.pem");

        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> serviceWithMissingKey.decryptarQrCode("someData"));
        assertEquals("Falha na autenticação dos dados: Código inválido ou chave incorreta.", exception.getMessage());
    }
}
