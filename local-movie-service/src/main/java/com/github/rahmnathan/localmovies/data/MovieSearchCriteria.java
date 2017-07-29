package com.github.rahmnathan.localmovies.data;

public class MovieSearchCriteria {
    private final int page;
    private final int itemsPerPage;
    private final String path;
    private final MovieClient client;

    private MovieSearchCriteria(String path, int page, int itemsPerPage, MovieClient client){
        this.path = path;
        this.page = page;
        this.itemsPerPage = itemsPerPage;
        this.client = client;
    }

    public MovieClient getClient() {
        return client;
    }

    public int getPage() {
        return page;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public String getPath() {
        return path;
    }

    public static class Builder {
        private int page;
        private int itemsPerPage;
        private String path;
        private MovieClient client;

        public static Builder newInstance(){
            return new Builder();
        }

        public Builder setClient(String client) {
            if(client != null)
                this.client = MovieClient.valueOf(client);
            return this;
        }

        public Builder setPage(Integer page) {
            if(page != null)
                this.page = page;
            else
                this.page = 0;
            return this;
        }

        public Builder setItemsPerPage(Integer itemsPerPage) {
            if(itemsPerPage != null)
                this.itemsPerPage = itemsPerPage;
            else
                this.itemsPerPage = 1000;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public MovieSearchCriteria build(){
            return new MovieSearchCriteria(path, page, itemsPerPage, client);
        }
    }
}
