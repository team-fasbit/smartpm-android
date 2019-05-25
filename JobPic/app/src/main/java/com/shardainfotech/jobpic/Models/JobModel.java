package com.shardainfotech.jobpic.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JobModel {

    @SerializedName("response")
    private Response response;
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static class Response {
        @SerializedName("total")
        private int total;
        @SerializedName("to")
        private int to;
        @SerializedName("per_page")
        private int per_page;
        @SerializedName("path")
        private String path;
        @SerializedName("last_page_url")
        private String last_page_url;
        @SerializedName("last_page")
        private int last_page;
        @SerializedName("from")
        private int from;
        @SerializedName("first_page_url")
        private String first_page_url;
        @SerializedName("data")
        private List<Data> data;
        @SerializedName("current_page")
        private int current_page;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getTo() {
            return to;
        }

        public void setTo(int to) {
            this.to = to;
        }

        public int getPer_page() {
            return per_page;
        }

        public void setPer_page(int per_page) {
            this.per_page = per_page;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getLast_page_url() {
            return last_page_url;
        }

        public void setLast_page_url(String last_page_url) {
            this.last_page_url = last_page_url;
        }

        public int getLast_page() {
            return last_page;
        }

        public void setLast_page(int last_page) {
            this.last_page = last_page;
        }

        public int getFrom() {
            return from;
        }

        public void setFrom(int from) {
            this.from = from;
        }

        public String getFirst_page_url() {
            return first_page_url;
        }

        public void setFirst_page_url(String first_page_url) {
            this.first_page_url = first_page_url;
        }

        public List<Data> getData() {
            return data;
        }

        public void setData(List<Data> data) {
            this.data = data;
        }

        public int getCurrent_page() {
            return current_page;
        }

        public void setCurrent_page(int current_page) {
            this.current_page = current_page;
        }
    }

    public static class Data {
        @SerializedName("ownerinfo")
        private Ownerinfo ownerinfo;
        @SerializedName("images")
        private List<Images> images;
        @SerializedName("updated_at")
        private String updated_at;
        @SerializedName("created_at")
        private String created_at;
        @SerializedName("status")
        private String status;
        @SerializedName("job_description")
        private String job_description;
        @SerializedName("address")
        private String address;
        @SerializedName("job_name")
        private String job_name;
        @SerializedName("user_id")
        private String user_id;
        @SerializedName("id")
        private int id;

        public Ownerinfo getOwnerinfo() {
            return ownerinfo;
        }

        public void setOwnerinfo(Ownerinfo ownerinfo) {
            this.ownerinfo = ownerinfo;
        }

        public List<Images> getImages() {
            return images;
        }

        public void setImages(List<Images> images) {
            this.images = images;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getJob_description() {
            return job_description;
        }

        public void setJob_description(String job_description) {
            this.job_description = job_description;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getJob_name() {
            return job_name;
        }

        public void setJob_name(String job_name) {
            this.job_name = job_name;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class Ownerinfo {
        @SerializedName("email")
        private String email;
        @SerializedName("id")
        private int id;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class Images {
        @SerializedName("image_name")
        private String image_name;
        @SerializedName("job_id")
        private String job_id;

        public String getImage_name() {
            return image_name;
        }

        public void setImage_name(String image_name) {
            this.image_name = image_name;
        }

        public String getJob_id() {
            return job_id;
        }

        public void setJob_id(String job_id) {
            this.job_id = job_id;
        }
    }
}
