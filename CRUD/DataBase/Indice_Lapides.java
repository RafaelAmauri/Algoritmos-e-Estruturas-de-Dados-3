package DataBase;
import java.io.RandomAccessFile;
// Quando uma entidade eh deletado em seu arquivo de dados, o campo lapide pode ser um grande 
// desperdicio de bytes. Essa classe aqui eh um indice indireto denso que armazena informacao em pares. 
// O primeiro dado (long), representa o tamanho do usuario deletado (em bytes), e o segundo dado (long) 
// representa sua localizacao em dados.db; isso faz com que campos lapide sejam sobreescritos caso um 
// novo usuario (ou update de usuario) gere <percentage for overwrite>% de
// ocupacao. Se <percentage for overwrite> for 80, por exemplo, a ocupacao deve ser de 80% ou mais.
// Esse arquivo tambem tem campos lapide, e eles sao indicados pelo long -1. A unica excecao à regra eh 
// o primeiro long do arquivo.
// Leia as funcoes create_grave_entry e search_empty_space para entender melhor o funcionamento.
public class Indice_Lapides
{
    String save_location;
    int percentage_for_overwrite;
    RandomAccessFile file;

    Indice_Lapides(int percentage_for_overwrite, String save_location)
    {
        try
        {
            this.save_location = save_location;
            open_file();

            if(file.length() == 0)
                file.writeLong(-1);
            
            close_file();

            this.percentage_for_overwrite = percentage_for_overwrite;
        }
        catch(Exception error)
        {
            error.printStackTrace();
        }
    }

    
    // Essa funcao eh usada para notificar esse indice que uma entidade foi deletado em 
    // seu arquivo de dados; Esse indice aqui vai armazenar onde esta esse espaco livre para 
    // reciclar os bytes. 

    // Esse indice trabalha em pares: 
    // [tamanho de um espaco vago em dados.db (-1 aqui indica lapide no indice)]
    // [localizacao do espaco vago em dados.db (Se o long anterior for -1, esse aqui vira a localizacao da lapide anterior do indice)]

    // Exemplo:
    // -1
    // 24 - 12
    // 15 - 29

    // O primeiro long do arquivo armazena a localizacao da lapide mais recente DESSE INDICE.
    // Ler esse valor nos permite pular diretamente para ela. Isso faz com que nao seja preciso ler 
    // o arquivo todo procurando por onde esta o -1, que indica um espaco vazio para insercao aqui

    // Em essencia: Recebe duas chaves e procura se tem uma lapide nesse indice. Se houver, sobreescreve 
    // a lapide. Se nao houver, escreve as novas chaves no fim do arquivo
    public void create_entry_grave(long size, long location_in_db_file)
    {
        try
        {
            open_file();

            // last_grave armazena campo lapide mais recente desse indice.
            long last_grave = file.readLong();

            // Se o primeiro long do indice for -1, eh pq esse indice esta sem campos lapide. Isso mostra
            // que os valores recebidos devem ser inseridos no final.
            if(last_grave == -1)
            {
                file.seek(file.length());
                // Escrevendo no indice o tamanho da entidade deletada e o local da sua lapide, respectivamente.
                file.writeLong(size);
                file.writeLong(location_in_db_file);
            }

            // Se o primeiro long do indice nao for -1, eh pq existe um campo lapide nesse indice, e o 
            // valor lido por last_grave nos leva diretamente a ele.
            else
            {   
                // Reescrevendo o campo lapide para nao desperdicar espaco nesse indice
                file.seek(last_grave);
                file.writeLong(size);
                
                // Sabendo que existe uma lapide (caiu no else acima), ler o valor da lapide anterior
                // do indice, pular para o inicio do arquivo e sobreescrever o primeiro long com a posicao 
                // da lapide anterior. Essa estrategia possibilita SEMPRE saber se existe uma lapide 
                // nesse arquivo, e o arquivo NUNCA vai crescer se nao for necessario. 
                // Em essencia, esse arquivo vira uma pilha de entradas de lapides no indice.
                long current_pos     = file.getFilePointer();
                long next_empty_spot = file.readLong();
                file.seek(0);
                file.writeLong(next_empty_spot);

                file.seek(current_pos);
                file.writeLong(location_in_db_file);
            }


            close_file();
        }
        catch(Exception error)
        {
            error.printStackTrace();
        }
    }


    // A funcao search_empty_space recebe um long (qual o tamanho da nova entidade em bytes) e procura
    // no indice se existe algum espaco vazio nele que tenha esse mesmo tamanho ou outro que a sobreescrita 
    // gere, no minimo, <percentage_for_overwrite>% de ocupacao. O campo lapide desse
    // indice eh lido como um long -1. Se ao inves de um tamanho for encontrado um -1,
    // eh pq esse campo eh uma lapide e nao deve ser considerado
    public long search_empty_space(long new_entity_size)
    {
        // O melhor candidato sera aquele que, no minimo, usa <percentage_for_replacement>% ou mais do 
        // seu espaco para armazenar o novo registro
        long best_candidate_value = -1;
        long best_candidate_location_file = -1; // Localizacao da lapide no arquivo de dados
        long best_candidate_location_index = -1; // Localizacao do byte inicial do melhor candidato no index

        // candidate_size representa o tamanho de um dos espacos vazios no arquivo de dados; Essa variavel
        // Serve para ser comparado com new_user_size para achar a melhor lapide no arquivo de dados.
        long candidate_size = -1; 
        try
        {
            open_file();

            // Pulando o long inicial que indica a ultima lapide inserida nesse indice.
            // Como so esta sendo feita uma busca por espacos no arquivo de dados, ela nao eh necessaria.
            file.seek(8);
            boolean flag = true;

            while( flag && (file.getFilePointer() <= file.length()-1) )
            {
                candidate_size = file.readLong();

                // Aqui eh checado se mais de <percentage for replacement>% do espaco vago vai 
                // ser usado pra armazenar a nova entidade
                if((100*new_entity_size/candidate_size >= this.percentage_for_overwrite))
                {
                    // Aqui verifica se o tamanho da entidade nova eh 100% compativel com um espaco vago
                    // existente no arquivo de dados
                    if(new_entity_size == candidate_size)
                    {
                        best_candidate_value = 100;
                        best_candidate_location_file = file.readLong();
                        flag = false;
                        
                        best_candidate_location_index = file.getFilePointer()-16;
                    }

                    // Se nao tiver um espaco 100% compativel, o melhor candidato vai ser o que gera maior
                    // ocupacao, desde que ela seja maior que <percentage for replacement>%
                    else if(100*new_entity_size/candidate_size > best_candidate_value)
                    {
                        best_candidate_value = 100*new_entity_size/candidate_size;
                        best_candidate_location_file = file.readLong();

                        best_candidate_location_index = file.getFilePointer()-16;
                    }
                }
                else
                    file.readLong();
            }

            // Depois de achado o melhor candidato a ser substituido, o campo lapide que antes tinha
            // uma entrada aqui vai ser excluido no arquivo de dados; Isso faz que seja necessario deletar a 
            // entrada dele nesse arquivo de lixo. Eh aqui que sao criadas as "lapides" desse indice de lixo
            if(best_candidate_location_index != -1)
                delete_grave_entry(best_candidate_location_index);
            
            close_file();
        }
        catch(Exception error)
        {
            error.printStackTrace();
        }
        return best_candidate_location_file;
    }


    // Essa funcao marca a posicao <location> como lapide para evitar desperdicio de bytes.
    // Ela faz isso lendo a lapide anterior que estava gravada no inicio do indice e armazenando o valor dela.
    // Apos fazer isso, ela sobreescreve o primeiro valor do indice como sendo o valor recebido 
    // pela funcao. Depois, ela volta para <location> e marca o inicio da entrada como -1, para 
    // sabermos que eh uma lapide na hora das buscas. Depois, armazena o valor da penultima lapide 
    // como segundo valor da entrada. Isso permite criarmos uma "pilha de lapides" ao juntarmos 
    // essa funcionalidade com o final da funcao <create_entry_grave>
    public void delete_grave_entry(long location)
    {
        try
        {
            file.seek(0);
            long last_grave_pos = file.readLong();
            file.seek(0);
            file.writeLong(location);

            file.seek(location);
            file.writeLong(-1);
            file.writeLong(last_grave_pos);
        }
        catch(Exception error)
        {
            error.printStackTrace();
        }
    }

    
    // Para debug, nao usar!!
    public void status()
    {
        try
        {
            open_file();

            long ultimo_deletado = file.readLong();
            System.out.println(ultimo_deletado + "\n");

            while(file.getFilePointer() < file.length())
            {
                long num1 = file.readLong();
                long num2 = file.readLong();

                System.out.println(num1 + " - " + num2 + "\n");
            }

            close_file();
        }
        catch(Exception error)
        {
            error.printStackTrace();
        }
    }


    public void open_file()
    {
        try
        {
            file = new RandomAccessFile(this.save_location, "rw");
        }
        catch(Exception error)
        {
            error.printStackTrace();
        }
    }


    public void close_file()
    {
        try
        {
            file.close();
        }
        catch(Exception error)
        {
            error.printStackTrace();
        }
    }
}