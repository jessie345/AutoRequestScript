package com.liushuo.auto.requester;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.liushuo.auto.constant.TextConstant;
import com.liushuo.auto.http.HttpClient;
import com.liushuo.auto.log.Log;
import com.liushuo.auto.util.ComicUtil;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class MaiMengRequester extends Requester {
    
    private static final String COMMENT_POST = "https://api-app.maimengjun.com/comment/commentPost";
    private static final String MM_COMIC_CATEGORIES = "https://api-app.maimengjun.com/comic/categories";
    private static final String MM_CATEGORY_COMIC_SUMMARY_LIST = "https://api-app.maimengjun.com/category/bookList";
    private static final String MM_CATEGORY_COMIC_DETAIL = "https://api-app.maimengjun.com/comicDetail";
    private static final String MM_COMIC_POST_LIST = "https://api-app.maimengjun.com/postList";
    
    private static final SimpleDateFormat sPrintDF = new SimpleDateFormat("yyyy年MM月dd日");
    
    public static void main(String[] args) {
        MaiMengRequester requester = new MaiMengRequester();
        // 请求所有分类
        requester.autoFetchComicCategories();
    }
    
    @Override
    public String mainBaseUrl() {
        return "https://api-app.maimengjun.com/";
    }
    
    private void autoFetchComicCategories() {
        Call<JsonElement> comicCategoriesCall = getHttpService().RetrofitGetApi(MM_COMIC_CATEGORIES, Collections.emptyMap());
        JsonObject comicCategoriesResult = HttpClient.executeHttpCall(comicCategoriesCall);
        JsonArray comicCategoryJson = ((JsonObject) (comicCategoriesResult.getAsJsonArray("data").get(0))).getAsJsonArray("categoryList");
        Log.log("成功获取所有分类数据,category list:" + comicCategoryJson);
        
        int categoryCount = comicCategoryJson.size();
        for (int i = 0; i < categoryCount; i++) {
            JsonObject category = (JsonObject) comicCategoryJson.get(i);
            autoEnterComicCategory(category);
        }
    }
    
    private void autoFetchComicCurrentYearPost(JsonObject comicDetail) {
        List<JsonObject> currentYearPosts = new ArrayList<>();
        
        long currentYearBeginTime = 0;
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
        try {
            currentYearBeginTime = df.parse("20180101000000").getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        String clubId = comicDetail.getAsJsonPrimitive("clubId").getAsString();
        
        int page = 1;
        int size = 20;
        int orderType = 2;
        
        while (true) {
            Map<String, String> options = new HashMap<>();
            options.put("clubId", clubId);
            options.put("page", page + "");
            options.put("size", size + "");
            options.put("orderType", orderType + "");
            Call<JsonElement> postListCall = getHttpService().RetrofitGetApi(MM_COMIC_POST_LIST, options);
            JsonObject postListResult = HttpClient.executeHttpCall(postListCall);
            if (postListResult == null) {
                Log.log("小组的帖子数据获取失败,cludId:" + clubId);
                break;
            }
            
            JsonArray postListJson = postListResult.getAsJsonArray("data");
            if (postListJson == null) {
                Log.log("小组的帖子数据获取失败,cludId:" + clubId);
                break;
            }
            
            if (postListJson.size() == 0) {
                break;
            }
            
            boolean hasNoPost = false;
            for (JsonElement obj : postListJson) {
                JsonObject onePost = (JsonObject) obj;
                String createTime = onePost.getAsJsonPrimitive("createTime").getAsString();
                long createTimeStamp = Long.parseLong(createTime);
                if (createTimeStamp < currentYearBeginTime) {
                    hasNoPost = true;
                    break;
                }
                
                currentYearPosts.add(onePost);
            }
            
            Log.log(String.format("成功获取小组内帖子数据,cludId=%s,page=%d,n秒后继续获取,获取的年内总帖子数量 %d", clubId, page, currentYearPosts.size()));
            
            if (hasNoPost) { // 没有年内发表的帖子
                break;
            }
            
            ComicUtil.simulateManualDelay();
            
            page++;
        }
        
        Log.log("小组内的帖子数据大概为 " + currentYearPosts.size() + ",cludId:" + clubId);
        
        autoReplyCurrentYearPosts(currentYearPosts);
    }
    
    private void autoReplyCurrentYearPosts(List<JsonObject> thisYearPosts) {
        int postsCount = thisYearPosts.size();
        for (int i = 0; i < postsCount; i++) {
            JsonObject obj = thisYearPosts.get(i);
            String id = obj.getAsJsonPrimitive("id").getAsString();
            String title = obj.getAsJsonPrimitive("title").getAsString();
            String createTime = obj.getAsJsonPrimitive("createTime").getAsString();
            
            Map<String, String> commentPostParams = new HashMap<>();
            commentPostParams.put("postsId", id);
            commentPostParams.put("content", TextConstant.TEXT);
            
            Call<JsonElement> commentPostCall = getHttpService().RetrofitPostFormApi(COMMENT_POST, commentPostParams, Collections.emptyMap());
            Log.log("自动回复帖子:" + title + ",create time:" + sPrintDF.format(new Date(Long.parseLong(createTime) * 1000)));
        }
    }
    
    private void autoEnterComicDetail(JsonObject comicSummary) {
        String bookId = comicSummary.getAsJsonPrimitive("id").getAsString();
        
        Map<String, String> options = new HashMap<>();
        options.put("id", bookId);
        Call<JsonElement> comicDetailCall = getHttpService().RetrofitGetApi(MM_CATEGORY_COMIC_DETAIL, options);
        JsonObject comicDetailResult = HttpClient.executeHttpCall(comicDetailCall);
        if (comicDetailResult == null) {
            return;
        }
        
        JsonObject comicDetailJson = comicDetailResult.getAsJsonObject("data");
        Log.log(String.format("成功获取漫画详情数据,id=%s", bookId));
        
        // 自动评论详情内的所有帖子
        autoFetchComicCurrentYearPost(comicDetailJson);
    }
    
    /**
     * 获取指定分类下的所有漫画列表
     *
     * @param category
     */
    private void autoEnterComicCategory(@NonNull JsonObject category) {
        
        List<JsonObject> comicSummaryList = new ArrayList<>();
        
        int page = 1;
        int size = 20;
        
        while (true) {
            String ename = category.getAsJsonPrimitive("ename").getAsString();
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put("id", "0");
            queryParams.put("ename", ename);
            queryParams.put("page", page + "");
            queryParams.put("size", size + "");
            
            
            Call<JsonElement> comicSummaryListCall = getHttpService().RetrofitGetApi(MM_CATEGORY_COMIC_SUMMARY_LIST, queryParams);
            JsonObject comicSummaryListResult = HttpClient.executeHttpCall(comicSummaryListCall);
            if (comicSummaryListResult == null) {
                Log.log("获取漫画列表失败,ename=" + ename);
                break;
            }
            
            JsonArray comicSummaryListJson = comicSummaryListResult.getAsJsonArray("data");
            if (comicSummaryListJson == null || comicSummaryListJson.size() == 0) {
                Log.log("无更多漫画数据,ename=" + ename);
                break;
            }
            
            for (JsonElement je : comicSummaryListJson) {
                comicSummaryList.add((JsonObject) je);
            }
            
            Log.log("成功获取分类下的第 " + page + "页数据,count=" + comicSummaryListJson.size() + ",延时n s后获取下一页数据,category:" + category);
            
            // 睡眠延时n s,模拟真实操作
            ComicUtil.simulateManualDelay();
            
            page++;
            
            if (page >= 1) {
                break;
            }
        }
        
        
        //循环进入漫画详情
        for (JsonObject summaryJson : comicSummaryList) {
            autoEnterComicDetail(summaryJson);
            
            // 睡眠延时n s,模拟真实操作
            ComicUtil.simulateManualDelay();
        }
    }
}
