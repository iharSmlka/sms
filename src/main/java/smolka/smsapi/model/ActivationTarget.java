package smolka.smsapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "TARGET_SERVICE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivationTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "CODE", unique = true)
    private String serviceCode;

    @Column(name = "SMSHUB_CODE", unique = true)
    private String smshubServiceCode;
}
