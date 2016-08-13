package it.grab.grabbit.grabbit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.math.BigInteger;
import java.util.ArrayList;

public class ProductInfo extends AppCompatActivity {

    ArrayList<Product> alternateProductsLst;
    Product currProduct;
    ImageView productImage;
    ImageView veganStamp;
    ImageView notTestedStamp;
    TextView productName;
    TextView productCompany;


    public void setExactSize(ImageView img, int x) {
        img.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
//        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#9ab7fb"));
        // in Activity#onCreate
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);

        // get current barcode
        Bundle bundle = getIntent().getExtras();
        String barcode = bundle.getString("barcode");

        // TODO: get current product info + alternatives

        // mock product + alternatives
        currProduct = new Product(barcode);
        alternateProductsLst = new ArrayList<Product>(4);
        for (int i = 0; i<4 ; i++){
            alternateProductsLst.add(new Product());
        }

        // update currProduct image+texts
        productImage = (ImageView)findViewById(R.id.product_img);
        if (false) { // item not found in DB
            productImage.setImageBitmap(currProduct.getImage());
        }

        productName = (TextView)findViewById(R.id.product_name);
        productName.setText(currProduct.getName());
        productCompany = (TextView)findViewById(R.id.product_company);
        productCompany.setText(currProduct.getCompany());

        // Adjust vegan / tested stamps
        veganStamp = (ImageView)findViewById(R.id.vegan);
        notTestedStamp = (ImageView)findViewById(R.id.not_tested);
        adjustStamps();

        // update the alternative images
        LinearLayout altProdsLayout = (LinearLayout)findViewById(R.id.alternative_products);

        for (int i = 0; i < alternateProductsLst.size(); i++) {
            Product altProd = alternateProductsLst.get(i);
            ImageView imageView = new ImageView(this);
            setExactSize(imageView, 300);
            imageView.setId(i);
            imageView.setImageBitmap(altProd.getImage());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            altProdsLayout.addView(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Product altProd = alternateProductsLst.get(view.getId());
                    switchCurrProduct(view.getId());
                }
            });
        }

    }

    void switchCurrProduct(int prodInd) {
        Product tmp = currProduct;
        currProduct = alternateProductsLst.get(prodInd);
        alternateProductsLst.set(prodInd, tmp);

        // update top image
        if (currProduct.getImage() == null) {
            productImage.setImageDrawable(getResources().getDrawable(R.drawable.nophoto));
        } else {
            productImage.setImageBitmap(currProduct.getImage());
        }
        adjustStamps();
        productCompany.setText(currProduct.getCompany());
        productName.setText(currProduct.getName());
        // update image in list
        ImageView lstProdImg = (ImageView)findViewById(prodInd);
        if (tmp.getImage() == null) {
            lstProdImg.setImageDrawable(getResources().getDrawable(R.drawable.nophoto));
        } else {
            lstProdImg.setImageBitmap(tmp.getImage());
        }
        setExactSize(lstProdImg, 300);

    }

    private void adjustStamps() {
        int notTested = currProduct.getStatus() % 2;
        int vegan = currProduct.getStatus() >> 1;
        int nt = (notTested == 1) ? R.drawable.not_tested : R.drawable.tested;
        int veg = (vegan == 1) ? R.drawable.vegan : R.drawable.not_vegan;
        veganStamp.setImageDrawable(getResources().getDrawable(veg));
        notTestedStamp.setImageDrawable(getResources().getDrawable(nt));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
