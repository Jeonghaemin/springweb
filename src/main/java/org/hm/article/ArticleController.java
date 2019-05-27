package org.hm.article;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import org.hm.book.chap11.Member;

@Controller
public class ArticleController {

	@Autowired
	ArticleDao articleDao;

	Logger logger = LogManager.getLogger();

	/**
	 * 글 목록
	 */
	@GetMapping("/article/list")
	public void articleList(
			@RequestParam(value = "page", defaultValue = "1") int page,
			Model model) {

		// 페이지당 행의 수와 페이지의 시작점
		final int COUNT = 100;
		int offset = (page - 1) * COUNT;

		List<Article> articleList = articleDao.listArticles(offset, COUNT);
		int totalCount = articleDao.getArticlesCount();
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("articleList", articleList);
	}

	/**
	 * 글 보기
	 */
	@GetMapping("/article/view")
	public void articleView(@RequestParam("articleId") String articleId,
			Model model) {
		Article article = articleDao.getArticle(articleId);
		model.addAttribute("article", article);
	}

	@GetMapping("/article/addForm")
	public String articleAddForm(HttpSession session) {
		Object memberObj = session.getAttribute("MEMBER");
		if (memberObj == null)
			// 세션에 MEMBER가 없을 경우 로그인 화면으로
			return "login/loginForm";

		return "article/addForm";
	}

	/**
	 * 글 등록
	 */
	@PostMapping("/article/add")
	public String articleAdd(Article article,
			@SessionAttribute("MEMBER") Member member){
		article.setUserId(member.getMemberId());
		article.setName(member.getName());
		articleDao.addArticle(article);
		return "redirect:/app/article/list";
}
	
	
	@GetMapping("/article/updateform")
	public String articleUpdateForm(@RequestParam("articleId") String articleId,
			@SessionAttribute("MEMBER") Member member, Model model) {
		Article article = articleDao.getArticle(articleId);
		if (!article.getUserId().equals(member.getMemberId())) {
			logger.debug("X", member.getName());
			return "redirect:/app/article/view?articleId=" + article.getArticleId();

		}
		model.addAttribute("article", article);
		return "article/updateform";
}
	
	@PostMapping("/article/update")
	public String articleUpdate(Article article) {
		articleDao.updateArticle(article);
		return "redirect:/app/article/view?articleId=" + article.getArticleId();
	}
	

	
	@GetMapping("/article/delete")
	public String articleDelete(@RequestParam("articleId") String articleId, 
			@SessionAttribute("MEMBER") Member member) {
		Article article = articleDao.getArticle(articleId);
		if (!article.getUserId().equals(member.getMemberId())) {
			logger.debug("X", member.getName());
			return "redirect:/app/article/view?articleId=" + article.getArticleId();
		}
		articleDao.deleteArticle(articleId);
		return "article/delete";
}

	
}