/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.steeplesoft.frenchpress.beans;

import com.steeplesoft.frenchpress.model.Comment;
import com.steeplesoft.frenchpress.model.Post;
import com.steeplesoft.frenchpress.service.PostService;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.inject.Model;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;
import javax.servlet.ServletRequest;
import javax.transaction.Transactional;

/**
 *
 * @author jdlee
 */
@Model
public class PostBean implements Serializable {

    @Inject
    private PostService postService;
    private Post post = new Post();
    private HtmlDataTable dataTable;
    private Comment comment = new Comment();

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public List<Post> getPosts(int limit) {
        if (!FacesContext.getCurrentInstance().getRenderResponse()) {
            return null;
        }

        return postService.getPosts(limit);
    }

    public void loadPost() {
        Post requestedPost = (Post) ((ServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getAttribute("post");
        post = (requestedPost != null) ? requestedPost : postService.getPost(post.getId());
    }

    public String update() {
        postService.updatePost(post);
        return Constants.VIEW_ADMIN_POSTS_INDEX;
    }

    public String save() {
        postService.createPost(post);
        return Constants.VIEW_ADMIN_POSTS_INDEX;
    }

    public String delete() {
        Post toDelete = (Post) dataTable.getRowData();
        postService.deletePost(toDelete);

        return null;
    }

    public HtmlDataTable getDataTable() {
        return dataTable;
    }

    public void setDataTable(HtmlDataTable dataTable) {
        this.dataTable = dataTable;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public String addComment() {
        loadPost();
        comment.setPost(post);
        post.getComments().add(comment);
        postService.updatePost(post);
        comment = new Comment();

        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(post.getSlug());
        } catch (IOException ex) {
        }
        return null;
    }

    public void format(ComponentSystemEvent event) throws AbortProcessingException {
        UIComponent uic = event.getComponent();
        if (uic instanceof UIOutput) {
            UIOutput output = (UIOutput) uic;
            String value = (String) output.getValue();
            value = value.replaceAll("\\n", "\n<br/>");
            output.setValue(value);
        }
    }
}