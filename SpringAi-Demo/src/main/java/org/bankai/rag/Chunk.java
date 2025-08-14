package org.bankai.rag;


/**
 * 分片包装实体类
 * @param <T>
 */
public class Chunk<T> {
    private final T content;
    private final Integer index;
    private final Integer total;

    private Chunk(Builder<T> builder) {
        this.content = builder.content;
        this.index = builder.index;
        this.total = builder.total;
    }

    public T getContent() {
        return content;
    }

    public int getIndex() {
        return index;
    }

    public int getTotal() {
        return total;
    }

    public static class Builder<T> {
        private T content;
        private Integer index;
        private Integer total;

        public Builder<T> content(T content) {
            this.content = content;
            return this;
        }

        public Builder<T> index(int index) {
            this.index = index;
            return this;
        }

        public Builder<T> total(int total) {
            this.total = total;
            return this;
        }

        public Chunk<T> build() {
            if (content == null) {
                throw new IllegalArgumentException("Content cannot be null");
            }
            return new Chunk<>(this);
        }
    }


}