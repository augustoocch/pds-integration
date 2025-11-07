package ar.com.uade.pds.final_project.notifications.model;

import ar.com.uade.pds.final_project.notifications.event.NotificationType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscribers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriberData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String address;

    @Enumerated(EnumType.STRING)
    private NotificationType channel;
}