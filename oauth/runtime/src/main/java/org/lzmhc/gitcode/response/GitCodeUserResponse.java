package org.lzmhc.gitcode.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.lzmhc.api.UserResponse;

public class GitCodeUserResponse implements UserResponse {
    @JsonProperty("id")
    private String id;
    @JsonProperty("login")
    private String login;
    @JsonProperty("login_name")
    private String loginName;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("email")
    private String email;
    @JsonProperty("avatar_url")
    private String avatarUrl;
    @JsonProperty("language")
    private String language;
    @JsonProperty("is_admin")
    private Boolean isAdmin;
    @JsonProperty("last_login")
    private String lastLogin;
    @JsonProperty("created")
    private String cteated;
    @JsonProperty("restricted")
    private Boolean restricted;
    @JsonProperty("active")
    private Boolean active;
    @JsonProperty("prohibit_login")
    private Boolean prohibitLogin;
    @JsonProperty("location")
    private String location;
    @JsonProperty("website")
    private String website;
    @JsonProperty("description")
    private String description;
    @JsonProperty("visibility")
    private String visibility;
    @JsonProperty("followers_count")
    private Long followersCount;
    @JsonProperty("following_count")
    private Long followingCount;
    @JsonProperty("starred_repos_count")
    private Long starredReposCount;
    @JsonProperty("username")
    private String username;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getCteated() {
        return cteated;
    }

    public void setCteated(String cteated) {
        this.cteated = cteated;
    }

    public Boolean getRestricted() {
        return restricted;
    }

    public void setRestricted(Boolean restricted) {
        this.restricted = restricted;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getProhibitLogin() {
        return prohibitLogin;
    }

    public void setProhibitLogin(Boolean prohibitLogin) {
        this.prohibitLogin = prohibitLogin;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Long getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Long followersCount) {
        this.followersCount = followersCount;
    }

    public Long getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(Long followingCount) {
        this.followingCount = followingCount;
    }

    public Long getStarredReposCount() {
        return starredReposCount;
    }

    public void setStarredReposCount(Long starredReposCount) {
        this.starredReposCount = starredReposCount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
