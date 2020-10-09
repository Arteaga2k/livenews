package dev.arteaga.breakingnews.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dev.arteaga.breakingnews.R
import dev.arteaga.breakingnews.remote.NewsDetails
import dev.arteaga.breakingnews.util.DateUtil.formatTo
import dev.arteaga.breakingnews.util.DateUtil.toDate


class NewsListAdapter(
    private val articles: List<NewsDetails.Article>,
    private val itemClickListener: NewsClickListener
) :
    RecyclerView.Adapter<NewsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = articles.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articles[position]
        holder.bind(item, itemClickListener)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val newsTitle: TextView = itemView.findViewById(R.id.newsTitle)
        private val newsDesc: TextView = itemView.findViewById(R.id.newsDesc)
        private val publishedAt: TextView = itemView.findViewById(R.id.tvPublishedAt)
        private val newsImage: ImageView = itemView.findViewById(R.id.newsImage)
        private val shareNews: ImageView = itemView.findViewById(R.id.newsShare)



        fun bind(article: NewsDetails.Article, itemClickListener: NewsClickListener) {

            newsTitle.text = article.title
            newsDesc.text = article.description
            publishedAt.text = article.publishedAt!!.toDate().formatTo("dd MMMM, hh:mm:ss")
            Picasso.get().load(article.urlToImage)
                .placeholder(R.drawable.news_placeholder)
                .error(R.drawable.news_placeholder)
                .into(newsImage)

            itemView.setOnClickListener { itemClickListener.onArticleClicked(article) }

            shareNews.setOnClickListener {
                itemClickListener.onShareClicked(article)
            }


        }
    }

    interface NewsClickListener {
        fun onArticleClicked(article: NewsDetails.Article)
        fun onShareClicked(article: NewsDetails.Article)
    }

}

