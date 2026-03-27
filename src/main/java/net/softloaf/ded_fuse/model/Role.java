package net.softloaf.ded_fuse.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "`role`")
public class Role {
    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;
}
