package app.iris.missingyou.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Entity
public class Push extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private Member member;

    @Column(nullable = false)
    private String deviceToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Region region;

    public Push (Member member) {
        this.member = member;
    }

    public Region getRegion() {
        return region;
    }
}
