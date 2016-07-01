package com.app3roodk;

import com.app3roodk.Schema.Comments;
import com.app3roodk.Schema.Offer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Date;
import java.util.HashMap;

public class UtilityFirebase {

    //region Comments
    static public DatabaseReference getComments(String offerId)
    {
        return FirebaseDatabase.getInstance().getReference("Comments").child(offerId);
    }

    static public DatabaseReference getComments(Offer offer)
    {
        return FirebaseDatabase.getInstance().getReference("Comments").child(offer.getObjectId());
    }

    static public void addNewComment(Offer offer,Comments comment, DatabaseReference.CompletionListener listener)
    {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Comments").child(offer.getObjectId());
        comment.setObjectId(myRef.push().getKey());
        myRef.child(comment.getObjectId()).setValue(comment,listener);
    }

    static public void updateComment(Comments comment)
    {
        FirebaseDatabase.getInstance().getReference("Comments").child(comment.getOfferId()).child(comment.getObjectId()).setValue(comment);
    }
    //endregion

    //region Offers
    static public DatabaseReference getOffer(String city,String category,String offerId)
    {
        return FirebaseDatabase.getInstance().getReference("Offers").child(city).child(category).child(offerId);
    }

    static public DatabaseReference getOffer(Offer offer)
    {
        return FirebaseDatabase.getInstance().getReference("Offers").child(offer.getCity()).child(offer.getCategoryName()).child(offer.getObjectId());
    }

    static public Query getActiveOffers(String city, String category)
    {
        return FirebaseDatabase.getInstance().getReference("Offers").child(city).child(category).orderByChild("endTime").startAt(UtilityGeneral.getCurrentDate(new Date()));
    }

    static public void updateOffer(Offer offer,HashMap<String, Object> childUpdates)
    {
        FirebaseDatabase.getInstance().getReference("Offers").child(offer.getCity()).child(offer.getCategoryName()).child(offer.getObjectId()).updateChildren(childUpdates);
    }
    //endregion

    //region Shops
    static public DatabaseReference getShop(String userId,String shopId)
    {
        return FirebaseDatabase.getInstance().getReference("Shops").child(userId).child(shopId);
    }
    static public DatabaseReference getShop(Offer offer)
    {
        return FirebaseDatabase.getInstance().getReference("Shops").child(offer.getUserId()).child(offer.getShopId());
    }
    //endregion
}
