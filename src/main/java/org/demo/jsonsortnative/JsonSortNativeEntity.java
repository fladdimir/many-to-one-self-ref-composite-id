package org.demo.jsonsortnative;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.vladmihalcea.hibernate.type.json.JsonType;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@TypeDef(name = "json", typeClass = JsonType.class)
public class JsonSortNativeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    // json for postgres & mysql compatibility; could also be postgres jsonb
    private Map<String, String> jsonMap = new HashMap<>();
}
