package org.joinmastodon.android.ui.displayitems;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.joinmastodon.android.R;
import org.joinmastodon.android.fragments.BaseStatusListFragment;
import org.joinmastodon.android.model.Status;
import org.joinmastodon.android.ui.drawables.SawtoothTearDrawable;
import org.joinmastodon.android.ui.utils.UiUtils;

import java.time.Instant;

import me.grishka.appkit.utils.V;

public class GapStatusDisplayItem extends StatusDisplayItem{
	public boolean loading;
	private Status status;

	public GapStatusDisplayItem(String parentID, BaseStatusListFragment<?> parentFragment, Status status){
		super(parentID, parentFragment);
		this.status=status;
	}

	@Override
	public Type getType(){
		return Type.GAP;
	}

	public static class Holder extends StatusDisplayItem.Holder<GapStatusDisplayItem>{
		public final ProgressBar progressTop, progressBottom;
		public final TextView textTop, gap, textBottom;
		public final View top, bottom;

		public Holder(Context context, ViewGroup parent){
			super(context, R.layout.display_item_gap, parent);
			progressTop=findViewById(R.id.progress_top);
			progressBottom=findViewById(R.id.progress_bottom);
			textTop=findViewById(R.id.text_top);
			textBottom=findViewById(R.id.text_bottom);
			top=findViewById(R.id.top);
			top.setOnClickListener(this::onViewClick);
			bottom=findViewById(R.id.bottom);
			bottom.setOnClickListener(this::onViewClick);
			gap=findViewById(R.id.gap);
			gap.setForeground(new SawtoothTearDrawable(context));
		}

		@Override
		public void onBind(GapStatusDisplayItem item){
			if(!item.loading){
				progressBottom.setVisibility(View.GONE);
				progressTop.setVisibility(View.GONE);
			}
			top.setClickable(!item.loading);
			bottom.setClickable(!item.loading);
			StatusDisplayItem next=getNextVisibleDisplayItem().orElse(null);
			Instant dateBelow=next instanceof HeaderStatusDisplayItem h ? h.status.createdAt
					: next instanceof ReblogOrReplyLineStatusDisplayItem l ? l.status.createdAt
					: null;
			String text=dateBelow!=null && item.status.createdAt!=null && dateBelow.isBefore(item.status.createdAt)
					? UiUtils.formatPeriodBetween(item.parentFragment.getContext(), dateBelow, item.status.createdAt)
					: null;
			gap.setText(text);
			int p=text==null ? V.dp(6) : V.dp(20);
			gap.setPadding(p, p, p, p);
		}

		private void onViewClick(View v){
			if(item.loading) return;
			boolean isTop=v==top;
			(isTop ? textTop : textBottom).startAnimation(UiUtils.opacityOut);
			V.setVisibilityAnimated((isTop ? progressTop : progressBottom), View.VISIBLE);
			item.parentFragment.onGapClick(this, isTop);
		}

		@Override
		public void onClick(){}
	}
}
