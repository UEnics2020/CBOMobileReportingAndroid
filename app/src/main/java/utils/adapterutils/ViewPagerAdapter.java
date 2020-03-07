package utils.adapterutils;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.cbo.cbomobilereporting.R;
import com.cbo.cbomobilereporting.ui.Show_Sample;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<String> mResources;

    public ViewPagerAdapter(Context mContext, ArrayList<String> mResources) {
        this.mContext = mContext;
        this.mResources = mResources;
    }

    @Override
    public int getCount() {
        return mResources.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.pager_item, container, false);
        PhotoView imageView = (PhotoView) itemView.findViewById(R.id.img_pager_item);
        //ImageView imageView = (ImageView) itemView.findViewById(R.id.img_pager_item);
        if (!mResources.get(position).equals("no_image")) {
           /* try {
                Bitmap b = BitmapFactory.decodeFile(mResources.get(position));
                imageView.setImageBitmap(b);
            }catch (Exception e){
                imageView.setImageResource(R.drawable.no_image);
            }
            usingSimpleImage(imageView);*/
            Glide.with(mContext)
                    .load(mResources.get(position))
                    .error(R.drawable.no_image)
                    .into(imageView);
        }else{
            imageView.setImageResource(R.drawable.no_image);
            //usingSimpleImage(imageView);
        }

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ( (Show_Sample) mContext).makeFullScreen();
            }
        });



        container.addView(itemView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ( (Show_Sample) mContext).makeFullScreen();
            }
        });

        return itemView;
    }




    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
