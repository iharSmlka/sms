package smolka.smsapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import smolka.smsapi.enums.SourceList;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "CURRENT_ACTIVATION")
@Data
@NamedEntityGraph(name = "currentActivation.all", attributeNodes = {
        @NamedAttributeNode("user"),
        @NamedAttributeNode(value = "country"),
        @NamedAttributeNode(value = "service")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CurrentActivation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    private User user;

    @Column(name = "NUMBER")
    private String number;

    @Column(name = "MESSAGE")
    private String message;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "COUNTRY_ID", referencedColumnName = "ID")
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "SERVICE_ID", referencedColumnName = "ID")
    private ActivationTarget service;

    @Column(name = "SOURCE_NAME")
    @Enumerated(value = EnumType.STRING)
    private SourceList source;

    @Column(name = "SOURCE_ID")
    private Long sourceId;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "START_DATE")
    private LocalDateTime createDate;

    @Column(name = "PLANNED_CLOSE_DATE")
    private LocalDateTime plannedFinishDate;

    @Column(name = "COST")
    private BigDecimal cost;
}
