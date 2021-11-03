package DataBase;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;

/*

Classe feita usando o conceito de salted hashes (https://en.wikipedia.org/wiki/Salt_(cryptography).
As senhas dos usuarios sao armazenadas como hash, e o a aleatoriedade do salt garante que cada hash
seja unica, mesmo com senhas iguais.

Na hora de efetuar login, a senha que o usuario inseriu vai ser computada na funcao de hash com o salt
existente para aquele usuario, e o resultado vai ser comparado com o existente no banco de dados. 
Se forem iguais, a senha inserida eh a certa, e o usuario sera logado

*/
public class SaltedHash
{
    //private int iterations = 10;
    private MessageDigest digest;
    private byte[] hashed_passwd  =  new byte[32];       // resultado
    private byte[] salt           =  new byte[32];       // salt usado na hora de computar a hash
    private SecureRandom random   =  new SecureRandom(); // a seed eh pega de /dev/urandom


    // Recebe apenas uma senha e gera um salt. Deve ser usado quando for criar uma nova senha para usuario.
    // Como a hash gerada tera 256 bits (32 bytes), a senha só pode ter 32 bytes de comprimento.
    // A verificacao se a senha tem 32 bytes ou menos deve ser implementada em outro menu, nao aqui.
    public SaltedHash(String passwd)
    {
        this.hash_password(passwd);
    }


    // Recebe a senha e um salt. Deve ser usado na hora de verificar um login
    public SaltedHash(String passwd, byte[] salt)
    {
        this.hash_password(passwd, salt);
    }


    // Gera um salt e chama a funcao hash_password(passwd, this.salt)
    private void hash_password(String passwd)
    {
        this.generate_salt();
        this.hash_password(passwd, this.salt);
    }


    // Cria uma instancia de MessageDigest e computa a hash.
    // Eu optei por usar SHA-256. Uma lista dos algoritmos disponiveis para o Java 8
    // pode ser encontrada no link abaixo, na seção Algorithms.

    // https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#MessageDigest
    private void hash_password(String passwd, byte[] salt)
    {
        try 
        {
            this.digest = MessageDigest.getInstance("SHA-256");
        }
        catch(Exception error)
        {
            error.printStackTrace();
        }

        // Incorporando salt ao digest
        this.digest.update(salt);
        // Computando hash com salt
        this.hashed_passwd = this.digest.digest(passwd.getBytes(StandardCharsets.UTF_8));
    }


    // Pega um valor aleatorio de /dev/urandom e le os primeiros 32 bytes.
    // Os valores sao armazenados em this.salt
    private void generate_salt()
    {
        random.nextBytes(this.salt);
    }


    // retorna this.salt
    public byte[] get_salt()
    {
        return this.salt;
    }


    // retorna this.hashed_passwd
    public byte[] get_passwd()
    {
        return this.hashed_passwd;
    }
}