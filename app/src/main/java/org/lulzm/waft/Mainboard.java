package org.lulzm.waft;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Mainboard.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Mainboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Mainboard extends Fragment {

    ImageView bt_qr,bt_nav,bt_money,bt_chat,bt_sos1,bt_sos2;
    Context context;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Mainboard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Mainboard.
     */
    public static Mainboard newInstance(String param1, String param2) {
        Mainboard fragment = new Mainboard();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_mainboard, container, false);
        //버튼 목록
        bt_qr = v.findViewById(R.id.bt_qr);
        bt_nav = v.findViewById(R.id.bt_nav);
        bt_money = v.findViewById(R.id.bt_money);
        bt_chat = v.findViewById(R.id.bt_chat);
        bt_sos1 = v.findViewById(R.id.bt_sos1);
        bt_sos2 = v.findViewById(R.id.bt_sos2);

        Glide.with(Mainboard.this)
                .load(R.drawable.main_qr_button)
                .into(bt_qr);
        Glide.with(Mainboard.this)
                .load(R.drawable.main_nav_button)
                .into(bt_nav);
        Glide.with(Mainboard.this)
                .load(R.drawable.main_money_button)
                .into(bt_money);
        Glide.with(Mainboard.this)
                .load(R.drawable.main_chat_button)
                .into(bt_chat);
        Glide.with(Mainboard.this)
                .load(R.drawable.main_sos_button)
                .into(bt_sos1);
        Glide.with(Mainboard.this)
                .load(R.drawable.main_sos_button2)
                .into(bt_sos2);

        Animation animeBottomToTop = AnimationUtils.loadAnimation(getActivity(),R.anim.anime_bottom_to_top);
        Animation animeTopToBottom = AnimationUtils.loadAnimation(getActivity(),R.anim.anime_top_to_bottom);
        Animation animeRightToleft = AnimationUtils.loadAnimation(getActivity(),R.anim.anime_right_to_left);
        Animation animeLeftToRight = AnimationUtils.loadAnimation(getActivity(),R.anim.anime_left_to_right);
        Animation animeFadein = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_out);

        bt_qr.setAnimation(animeTopToBottom);
        bt_nav.setAnimation(animeRightToleft);
        bt_money.setAnimation(animeLeftToRight);
        bt_chat.setAnimation(animeFadein);
        bt_sos1.setAnimation(animeLeftToRight);
        bt_sos2.setAnimation(animeRightToleft);
        // Inflate the layout for this fragment
        return v;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
