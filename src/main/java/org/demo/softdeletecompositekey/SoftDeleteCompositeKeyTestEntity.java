package org.demo.softdeletecompositekey;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import org.demo.softdeletecompositekey.SoftDeleteCompositeKeyTestEntity.TestCompositeEntityId;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Where(clause = "deleted=false")
@IdClass(TestCompositeEntityId.class)
@SQLDelete(sql = "UPDATE soft_delete_composite_key_test_entity SET deleted=true WHERE key1=? AND key2=?")
@Entity
public class SoftDeleteCompositeKeyTestEntity {

    @Id
    private Long key1;

    @Id
    private String key2;

    private boolean deleted;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestCompositeEntityId implements Serializable {
        private Long key1;
        private String key2;
    }
}
