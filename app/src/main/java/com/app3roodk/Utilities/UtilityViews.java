package com.app3roodk.Utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app3roodk.Constants;
import com.app3roodk.R;
import com.app3roodk.Schema.Shop;
import com.app3roodk.Schema.User;
import com.app3roodk.UI.About.AboutActivity;
import com.app3roodk.UI.FavoritesCards.FavoritesActivity;
import com.app3roodk.UI.Feedback.FeedbackActivity;
import com.app3roodk.UI.MainActivity.MainActivity;
import com.app3roodk.UI.Offer.AddNewOfferActivity;
import com.app3roodk.UI.Shop.ListShopsActivity;
import com.app3roodk.UI.Shop.ShopActivity;
import com.app3roodk.UtilityFirebase;
import com.app3roodk.UtilityGeneral;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class UtilityViews {

    //region NavigationDrawer
    static public void initNavigationDrawer(final Activity activity, final int activityId) {
        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                UtilityViews.NavigationDrawerClicks(activity, id, activityId);
                DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer);
                drawer.closeDrawer(GravityCompat.END);
                return true;
            }
        });
        hideDrawerItems(activity, activityId);
        ((DrawerLayout) activity.findViewById(R.id.drawer)).setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                hideDrawerItems(activity, activityId);
                showUserInfoNavigationDrawer(activity);
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    static private void NavigationDrawerClicks(final Activity activity, int id, final int activityId) {
        if (activityId == id) return;

        if (id == R.id.action_add_offers) {
            if (UtilityGeneral.getNumberOfAvailableOffers(activity, UtilityGeneral.getCurrentYearAndWeek()) <= 0)
                Snackbar.make(activity.findViewById(android.R.id.content), "عفواً لايمكنك عمل أكثر من " + String.valueOf(Constants.NUMBER_OF_OFFERS_PER_WEEK) + " عروض فى الإسبوع", Snackbar.LENGTH_LONG).show();
            else
                activity.startActivityForResult(new Intent(activity, AddNewOfferActivity.class), 222);
        } else if (id == R.id.action_signin) {
            signingClick(activity);
        } else if (id == R.id.action_new_shop) {
            activity.startActivityForResult(new Intent(activity, ShopActivity.class), 222);
        } else if (id == R.id.action_home) {
            activity.startActivityForResult(new Intent(activity, MainActivity.class), 222);
        } else if (id == R.id.action_view_my_shop) {
            activity.startActivityForResult(new Intent(activity, ListShopsActivity.class), 222);
        } else if (id == R.id.action_logout) {
            AuthUI.getInstance()
                    .signOut(activity)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            UtilityGeneral.removeShop(activity);
                            UtilityGeneral.removeUser(activity);
                            hideDrawerItems(activity, activityId);
                        }
                    });
        } else if (id == R.id.action_facebookPage) {
            try {
                activity.getPackageManager().getPackageInfo("com.facebook.katana", 0);
                activity.startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/583145478505894")), 222);
            } catch (Exception e) {
                activity.startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/3roodk")), 222);
            }
        } else if (id == R.id.action_favorites) {
            activity.startActivityForResult(new Intent(activity, FavoritesActivity.class), 222);
        } else if (id == R.id.action_feedback) {
            activity.startActivityForResult(new Intent(activity, FeedbackActivity.class), 222);
        } else if (id == R.id.action_aboutapp) {
            activity.startActivityForResult(new Intent(activity, AboutActivity.class), 222);
        }
    }

    static private void hideDrawerItems(Activity activity, int checkedId) {
        Menu nav_Menu = ((NavigationView) activity.findViewById(R.id.nav_view)).getMenu();
        nav_Menu.findItem(R.id.action_aboutapp).setVisible(false);
        //Visibility of Nav items
        if (checkedId == -1) {
            nav_Menu.findItem(R.id.action_home).setChecked(true);
            nav_Menu.findItem(R.id.action_home).setChecked(false);
        } else
            nav_Menu.findItem(checkedId).setChecked(true);
        if (UtilityGeneral.isRegisteredUser(activity)) {
            nav_Menu.findItem(R.id.action_signin).setVisible(false);
            nav_Menu.findItem(R.id.action_logout).setVisible(true);
            if (UtilityGeneral.isShopExist(activity)) {
                nav_Menu.findItem(R.id.action_new_shop).setVisible(false);
                nav_Menu.findItem(R.id.action_add_offers).setVisible(true);
                nav_Menu.findItem(R.id.action_view_my_shop).setVisible(true);
            } else {
                nav_Menu.findItem(R.id.action_new_shop).setVisible(true);
                nav_Menu.findItem(R.id.action_add_offers).setVisible(false);
                nav_Menu.findItem(R.id.action_view_my_shop).setVisible(false);
            }
        } else {
            nav_Menu.findItem(R.id.action_signin).setVisible(true);
            nav_Menu.findItem(R.id.action_logout).setVisible(false);
            nav_Menu.findItem(R.id.action_new_shop).setVisible(false);
            nav_Menu.findItem(R.id.action_add_offers).setVisible(false);
            nav_Menu.findItem(R.id.action_view_my_shop).setVisible(false);
        }
    }

    static private void showUserInfoNavigationDrawer(final Activity activity) {
        //show info of the user
        try {
            final NavigationView mNavigationView = (NavigationView) activity.findViewById(R.id.nav_view);
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                if (UtilityGeneral.isRegisteredUser(activity) && UtilityGeneral.isShopExist(activity)) {
                    ((TextView) mNavigationView.findViewById(R.id.txtNavNumOfOfers)).setText("يمكنك عمل " + String.valueOf(UtilityGeneral.getNumberOfAvailableOffers(activity, UtilityGeneral.getCurrentYearAndWeek())) + " عروض فى  هذا الإسبوع");
                } else
                    ((TextView) mNavigationView.findViewById(R.id.txtNavNumOfOfers)).setText("");
                ((TextView) mNavigationView.findViewById(R.id.txtNavName)).setText(auth.getCurrentUser().getDisplayName());
                ((TextView) mNavigationView.findViewById(R.id.txtNavEmail)).setText(auth.getCurrentUser().getEmail());
                if (auth.getCurrentUser().getPhotoUrl() != null)
                    Glide.with(activity)
                            .load(auth.getCurrentUser().getPhotoUrl())
                            .asBitmap()
                            .into(new BitmapImageViewTarget((ImageView) mNavigationView.findViewById(R.id.imgNavProfile)) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(activity.getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    ((ImageView) mNavigationView.findViewById(R.id.imgNavProfile)).setImageDrawable(circularBitmapDrawable);
                                }
                            });
                else
                    Glide.with(activity)
                            .load(R.drawable.defaultavatar)
                            .asBitmap()
                            .into(new BitmapImageViewTarget((ImageView) mNavigationView.findViewById(R.id.imgNavProfile)) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(activity.getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    ((ImageView) mNavigationView.findViewById(R.id.imgNavProfile)).setImageDrawable(circularBitmapDrawable);
                                }
                            });
            } else {
                ((TextView) mNavigationView.findViewById(R.id.txtNavNumOfOfers)).setText("");
                ((TextView) mNavigationView.findViewById(R.id.txtNavName)).setText("");
                ((TextView) mNavigationView.findViewById(R.id.txtNavEmail)).setText("");
                Glide.with(activity)
                        .load(R.drawable.logo)
                        .asBitmap()
                        .into(new BitmapImageViewTarget((ImageView) mNavigationView.findViewById(R.id.imgNavProfile)) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(activity.getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                ((ImageView) mNavigationView.findViewById(R.id.imgNavProfile)).setImageDrawable(circularBitmapDrawable);
                            }
                        });
            }
        } catch (Exception ignored) {
        }
    }
    //endregion

    //region Signing
    private static User signingUser;

    static public void signingClick(Activity activity) {
        signingUser = new User();
        activity.startActivityForResult(
                UtilityFirebase.getAuthIntent(),
                Constants.RC_SIGN_IN);
    }

    static public void signingResult(int resultCode, final Activity activity, final IValidateUser iValidateUser) {
        try {
            if (resultCode == Activity.RESULT_OK) {
                setLoadingVisibility(View.VISIBLE, activity, iValidateUser);
                FirebaseUser acct = FirebaseAuth.getInstance().getCurrentUser();
                signingUser.setObjectId(acct.getUid());
                signingUser.setEmail(acct.getEmail());
                signingUser.setName(acct.getDisplayName());
                signingUser.setNotificationToken(FirebaseInstanceId.getInstance().getToken());
                UtilityFirebase.getUser(acct.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User temp = dataSnapshot.getValue(User.class);
                        if (temp == null) {
                            UtilityFirebase.getUser(signingUser.getObjectId()).setValue(signingUser, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        Toast.makeText(activity, "حصل مشكله شوف النت ", Toast.LENGTH_SHORT).show();
                                    } else {
                                        UtilityGeneral.saveUser(activity, signingUser);
                                    }
                                    setLoadingVisibility(View.GONE, activity, iValidateUser);
                                }
                            });
                        } else {
                            signingUser = temp;
                            UtilityFirebase.getUserShops(signingUser.getObjectId()).addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            try {
                                                ArrayList<Shop> shops = new ArrayList<>();
                                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                                                    shops.add(dataSnapshot1.getValue(Shop.class));
                                                UtilityGeneral.saveShops(activity, shops);
                                            } catch (Exception ignored) {
                                            }
                                            UtilityGeneral.saveUser(activity, signingUser);
                                            setLoadingVisibility(View.GONE, activity, iValidateUser);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            setLoadingVisibility(View.GONE, activity, iValidateUser);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                Toast.makeText(activity, "فشل فى تسجيل الدخول", Toast.LENGTH_LONG).show();
                setLoadingVisibility(View.GONE, activity, iValidateUser);
            }
        } catch (Exception ignored) {
        }
    }

    static private ProgressDialog signingProgress;

    static private void setLoadingVisibility(int visibility, Activity activity, IValidateUser iValidateUser) {
        if (visibility == View.VISIBLE) {
            if (!(signingProgress != null && signingProgress.isShowing())) {
                signingProgress = ProgressDialog.show(activity, "تسجيل الدخول",
                        "جاري التحميل...", true);
            }
        } else if (visibility == View.GONE) {
            if (iValidateUser != null) iValidateUser.validateUser();
            if (signingProgress.isShowing()) signingProgress.dismiss();
        }
    }
    //endregion
}
