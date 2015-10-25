package net.osmand.plus.mapcontextmenu.details;

import android.view.View;

import net.osmand.plus.OsmandApplication;
import net.osmand.plus.helpers.SearchHistoryHelper.HistoryEntry;
import net.osmand.plus.mapcontextmenu.MenuBuilder;

public class HistoryMenuBuilder extends MenuBuilder {
	private final HistoryEntry entry;

	public HistoryMenuBuilder(OsmandApplication app, final HistoryEntry entry) {
		super(app);
		this.entry = entry;
	}

	private void buildRow(View view, int iconId, String text, int textColor) {
		buildRow(view, getRowIcon(iconId), text, textColor);
	}

	@Override
	public void build(View view) {
		super.build(view);

		for (PlainMenuItem item : plainMenuItems) {
			buildRow(view, item.getIconId(), item.getText(), 0);
		}
	}
}
