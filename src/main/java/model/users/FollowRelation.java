package model.users;

import java.time.LocalDate;

/**
 * Representa uma relação de acompanhamento (seguidor/seguido) entre dois utilizadores no sistema.
 * Esta classe serve como um modelo relacional intermédio para registar quem está a seguir quem
 * e capturar o momento exato (carimbo temporal) em que o vínculo de interesse foi estabelecido.
 */
public class FollowRelation {

    /**
     * O utilizador que tomou a iniciativa de seguir (o emissor da relação).
     */
    private User follower;

    /**
     * O utilizador que está a ser seguido por outrem (o recetor da relação).
     */
    private User followed;

    /**
     * A data cronológica em que a relação de seguimento foi criada e guardada no sistema.
     */
    private LocalDate followDate;

    /**
     * Constrói e inicializa uma nova instância de relação de seguimento entre dois utilizadores.
     * Atribui automaticamente a data corrente do sistema ({@link LocalDate#now()}) como o
     * momento de criação do vínculo.
     *
     * @param follower O utilizador que passa a ser o seguidor.
     * @param followed O utilizador que passa a ser o seguido.
     */
    public FollowRelation(User follower, User followed) {
        this.follower = follower;
        this.followed = followed;
        this.followDate = LocalDate.now();
    }
}