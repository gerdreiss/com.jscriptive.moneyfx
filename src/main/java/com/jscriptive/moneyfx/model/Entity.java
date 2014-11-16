package com.jscriptive.moneyfx.model;

/**
 * Created by jscriptive.com on 15/11/14.
 */
public class Entity {

    public static final String MFX_FIELD_PREFIX = "mfx_";
    public static final String ID_FIELD = MFX_FIELD_PREFIX + "id";

    private final String repositoryName;

    private Long id;

    protected Entity(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity)) return false;

        Entity entity = (Entity) o;

        if (id != null ? !id.equals(entity.id) : entity.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
