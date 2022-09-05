package com.newsapp.newswebappui.controller;

import com.news.app.newswebapp.exception.CategoryNotFoundException;
import com.news.app.newswebapp.exception.InvalidParameterException;
import com.news.app.newswebapp.exception.NewsNotFoundException;
import com.news.app.newswebapp.model.Article;
import com.news.app.newswebapp.model.Category;
import com.news.app.newswebapp.service.NewsApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is responsible for providing rest endpoints for UI to call and interact
 * with News API Application jar
 */
@Controller
public class NewsApiUIController {
    /**
     * Logger class for UI Controller
     */
    private Logger logger = Logger.getLogger(getClass().getName());

    /**
     * Supported Categories for Newsapi.org
     */
    @Value("${api.categories}")
    private List<String> categoriesOptions;

    /**
     * Supported Countries for Newsapi.org
     */
    @Value("${api.countries}")
    private List<String> countriesOptions;

    /**
     * Business Service fto intearct with Newsapi.org
     */
    private NewsApiService newsApiService;

    /**
     * Current Request class object to extract User Country Information
     */
    private HttpServletRequest request;

    /**
     * Constructor Injection for News API controller
     *
     * @param newsApiService
     * @param request
     */
    public NewsApiUIController(NewsApiService newsApiService, HttpServletRequest request) {
        this.newsApiService = newsApiService;
        this.request = request;
    }


    /**
     * Rest end point to check health of Application
     *
     * @return ResponseEntity
     */
    @GetMapping(value = "/health")
    public ResponseEntity health() {
        return ResponseEntity.ok("Status:OK");
    }

    /**
     * This Rest end point returns list of news article depending upon Country and category
     *
     * @param country
     * @param category
     * @return
     * @throws NewsNotFoundException
     */
    @GetMapping(path = {"/headlines", "/"})
    public String getTopHeadlines(Model model) throws NewsNotFoundException {
        try {

               String country = request.getLocale().getCountry();


            List<Article> articles = newsApiService.getHeadlines(null, country);
            model.addAttribute("articles", articles);
            model.addAttribute("country", country);
            String headLines = "Today's Headlines Country:" + country.toUpperCase();
            model.addAttribute("heading", headLines);
            model.addAttribute("categoriesOptions", categoriesOptions);
            model.addAttribute("countriesOptions", countriesOptions);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.toString());
            e.printStackTrace();
            model.addAttribute("error", true);
            model.addAttribute("message", e.getMessage());
        }
        return "newsfeed";
    }

    @GetMapping(value="/headliness")
    public String getTopHeadliness(Model model, @RequestParam(required = false) String country, @RequestParam(required = false) String category) throws NewsNotFoundException {
        try {
            if (StringUtils.isEmpty(country)) {
                country = request.getLocale().getCountry();
            }
            Category categoryEnum = null;
            try {
                if (!StringUtils.isEmpty(category))
                    categoryEnum = Category.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException exception) {
                throw new CategoryNotFoundException("Category := " + category + " not found");
            }
            List<Article> articles = newsApiService.getHeadlines(categoryEnum, country);
            model.addAttribute("articles", articles);
            model.addAttribute("country", country);
            String headLines = "Today's Headlines Country:" + country.toUpperCase();
            if (!StringUtils.isEmpty(category))
                headLines = headLines.concat(" Category:" + category.toUpperCase());
            model.addAttribute("heading", headLines);
            model.addAttribute("categoriesOptions", categoriesOptions);
            model.addAttribute("countriesOptions", countriesOptions);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.toString());
            e.printStackTrace();
            model.addAttribute("error", true);
            model.addAttribute("message", e.getMessage());
        }
        return "newsfeed";
    }

    /**
     * This rest endpoint provides search functionality across the news API on all allowed parameters
     *
     * @param allParams
     * @return
     * @throws NewsNotFoundException
     */
    @GetMapping(value = "search")
    public String search(Model model, @RequestParam Map<String, String> allParams) {
        try {
            String pageValue = allParams.get("page");
            int page = 1;
            if (pageValue != null) {
                page = Integer.valueOf(pageValue);
            }
            if (page <= 0) {
                throw new InvalidParameterException("Page " + page + " is not a valid parameter");
            }
            allParams.put("page", pageValue);
            List<Article> searchedArticles = newsApiService.search(allParams);
            model.addAttribute("articles", searchedArticles);
            model.addAttribute("categoriesOptions", categoriesOptions);
            model.addAttribute("q", allParams.get("q"));
            model.addAttribute("heading", newsApiService.getHeading(allParams));
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.toString());
            e.printStackTrace();
            model.addAttribute("error", true);
            model.addAttribute("message", e.getMessage());
        }
        return "search";
    }

    /**
     * Rest end point to navigate to search page
     *
     * @param model
     * @return
     */
    @GetMapping(value = "searchPage")
    public String searchPage(Model model) {
        return "search";
    }

    @GetMapping(value = "newsfeed")
    public String newsFeed(Model model) {
        return "newsfeed";
    }


    /**
     * Rest end point to getUser History
     *
     * @param model
     * @return
     */
    @GetMapping(value = "search/history")
    public String getHistory(Model model) {
        try {
            model.addAttribute("heading", "Last 5 Search History");
            model.addAttribute("history", newsApiService.getLastFiveSearch());
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.toString());
            e.printStackTrace();
            model.addAttribute("error", true);
            model.addAttribute("message", e.getMessage());
        }
        return "searchHistory";
    }

}