package com.app3roodk;

import android.content.Context;

import com.app3roodk.Schema.Comments;
import com.app3roodk.Schema.Offer;
import com.app3roodk.Schema.Shop;
import com.app3roodk.Schema.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class UtilityFirebase {

    //region Comments
    static public DatabaseReference getComments(String offerId) {
        return FirebaseDatabase.getInstance().getReference("Comments").child(offerId);
    }

    static public DatabaseReference getComments(Offer offer) {
        return FirebaseDatabase.getInstance().getReference("Comments").child(offer.getObjectId());
    }

    static public void addNewComment(Offer offer, Comments comment, DatabaseReference.CompletionListener listener) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Comments").child(offer.getObjectId());
        comment.setObjectId(myRef.push().getKey());
        myRef.child(comment.getObjectId()).setValue(comment, listener);
    }

    static public void removeOfferComments(String offerId)
    {
        FirebaseDatabase.getInstance().getReference("Comments").child(offerId).removeValue();
    }

    static public void updateComment(Comments comment, String userId,boolean like) {
        if (like) {
            FirebaseDatabase.getInstance().getReference("Comments").child(comment.getOfferId()).child(comment.getObjectId()).child("userLike").child(userId).setValue("Like");
            FirebaseDatabase.getInstance().getReference("Comments").child(comment.getOfferId()).child(comment.getObjectId()).child("userDislike").child(userId).removeValue();
        }
        else
        {
            FirebaseDatabase.getInstance().getReference("Comments").child(comment.getOfferId()).child(comment.getObjectId()).child("userDislike").child(userId).setValue("Dislike");
            FirebaseDatabase.getInstance().getReference("Comments").child(comment.getOfferId()).child(comment.getObjectId()).child("userLike").child(userId).removeValue();
        }
    }
    //endregion

    //region Offers
    static public DatabaseReference getOffer(String city, String category, String offerId) {
        return FirebaseDatabase.getInstance().getReference("Offers").child(city).child(category).child(offerId);
    }

    static public DatabaseReference getOffer(Offer offer) {
        return FirebaseDatabase.getInstance().getReference("Offers").child(offer.getCity()).child(offer.getCategoryName()).child(offer.getObjectId());
    }

    static public Query getActiveOffers(String city, String category) {
        return FirebaseDatabase.getInstance().getReference("Offers").child(city).child(category).orderByChild("endTime").startAt(UtilityGeneral.getCurrentDate(new Date()));
    }

    static public void createNewOffer(Offer offer,DatabaseReference.CompletionListener listener)
    {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Offers").child(offer.getCity()).child(offer.getCategoryName());
        offer.setObjectId(myRef.push().getKey());
        myRef.child(offer.getObjectId()).setValue(offer,listener);
    }

    static public void increaseOfferViewsNum(Offer offer) {
        FirebaseDatabase.getInstance().getReference("Offers").child(offer.getCity()).child(offer.getCategoryName()).child(offer.getObjectId()).child("viewNum").setValue(offer.getViewNum() + 1);
    }

    static public Query getOffersForSpecificShop(Shop shop, String category) {
        return FirebaseDatabase.getInstance().getReference("Offers").child(shop.getCity()).child(category)
                .orderByChild("shopId").startAt(shop.getObjectId()).endAt(shop.getObjectId());
    }

    static public void updateOffer(Offer offer, HashMap<String, Object> childUpdates) {
        FirebaseDatabase.getInstance().getReference("Offers").child(offer.getCity()).child(offer.getCategoryName()).child(offer.getObjectId()).updateChildren(childUpdates);
    }

    static public void removeOffer(Offer offer, DatabaseReference.CompletionListener listener) {
        FirebaseDatabase.getInstance().getReference("Offers").child(offer.getCity()).child(offer.getCategoryName()).child(offer.getObjectId()).removeValue(listener);
    }

    static public void updateOfferUserNotificationToken(final Offer offer) {
        FirebaseDatabase.getInstance().getReference("Users").child(offer.getUserId()).child("notificationToken").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseDatabase.getInstance().getReference("Offers").child(offer.getCity()).child(offer.getCategoryName()).child(offer.getObjectId()).child("userNotificationToken").setValue(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //endregion

    //region Shops
    static public DatabaseReference getShop(String userId, String shopId) {
        return FirebaseDatabase.getInstance().getReference("Shops").child(userId).child(shopId);
    }

    static public DatabaseReference getShop(Offer offer) {
        return FirebaseDatabase.getInstance().getReference("Shops").child(offer.getUserId()).child(offer.getShopId());
    }

    static public void createNewShop(Shop shop,String userId, DatabaseReference.CompletionListener listener)
    {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Shops").child(userId);
        shop.setObjectId(myRef.push().getKey());
        myRef.child(shop.getObjectId()).setValue(shop,listener);
    }

    static public void updateShop(String userId, Shop shop, DatabaseReference.CompletionListener listener) {
        FirebaseDatabase.getInstance().getReference("Shops").child(userId).child(shop.getObjectId()).setValue(shop, listener);
    }

    static public void getUserShops(final Context context, String userId){
        FirebaseDatabase.getInstance().getReference("Shops").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Shop> shops = new ArrayList<>();
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext())
                    shops.add(iterator.next().getValue(Shop.class));
                UtilityGeneral.saveShops(context, shops);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //endregion

    //region Users
    static public void updateUserNotificationToken(final Context context, final User user, String notificationToken)
    {
        user.setNotificationToken(notificationToken);
        FirebaseDatabase.getInstance().getReference("Users").child("notificationToken").setPriority(notificationToken, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                UtilityGeneral.saveUser(context,user);
            }
        });

    }
    //endregion

}
