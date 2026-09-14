package com.alphaver.asset;

import java.awt.image.BufferedImage;

final class CypressLookArt {
	private CypressLookArt() {}

	static final int ROW_DISABLED = 46;
	static final int ROW_NORMAL = 66;
	static final int ROW_HIGHLIGHTED = 86;

	private static final int ICON_THRESHOLD = 64;

	private static final double GLOW_INNER = 0.45;

	static final double NEBULA_INNER = 0.7;

	static BufferedImage region(BufferedImage sheet, int x, int y, int w, int h) {
		BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		for (int yy = 0; yy < h; yy++) {
			for (int xx = 0; xx < w; xx++) {
				out.setRGB(xx, yy, sheet.getRGB(x + xx, y + yy));
			}
		}
		return out;
	}

	static BufferedImage squeeze(BufferedImage row, int width) {
		int left = width / 2;
		int right = width - left;
		int h = row.getHeight();
		BufferedImage out = new BufferedImage(width, h, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < left; x++) {
				out.setRGB(x, y, row.getRGB(x, y));
			}
			for (int x = 0; x < right; x++) {
				out.setRGB(left + x, y, row.getRGB(row.getWidth() - right + x, y));
			}
		}
		return out;
	}

	static BufferedImage iconButton(BufferedImage icon, BufferedImage btaRow, BufferedImage cypressRow) {
		int w = icon.getWidth();
		int h = icon.getHeight();
		if (btaRow.getHeight() != h || cypressRow.getHeight() != h) {
			return null;
		}
		BufferedImage plain = squeeze(btaRow, w);
		BufferedImage out = squeeze(cypressRow, w);
		for (int y = 2; y < h - 3; y++) {
			for (int x = 2; x < w - 2; x++) {
				int pixel = icon.getRGB(x, y);
				if (difference(pixel, plain.getRGB(x, y)) > ICON_THRESHOLD) {
					out.setRGB(x, y, pixel);
				}
			}
		}
		return out;
	}

	static BufferedImage hotbarTab(BufferedImage gui, int selected) {
		int outline = gui.getRGB(0, 10);
		int frame = gui.getRGB(1, 10);
		int fill = gui.getRGB(10, 10);
		int lit = gui.getRGB(1, 30);
		int unlit = 0xC0000000 | (outline & 0xFFFFFF);
		BufferedImage tab = new BufferedImage(6, 22, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < 22; y++) {
			for (int x = 0; x < 6; x++) {
				int colour;
				if (x == 0 || x == 5 || y == 0 || y == 21) {
					colour = outline;
				} else if (x == 1 || y == 1 || y == 20) {
					colour = frame;
				} else {
					colour = fill;
				}
				tab.setRGB(x, y, colour);
			}
		}
		for (int pip = 0; pip < 4; pip++) {
			for (int y = 2 + 5 * pip; y <= 4 + 5 * pip; y++) {
				for (int x = 2; x <= 4; x++) {
					tab.setRGB(x, y, pip == selected ? lit : unlit);
				}
			}
		}
		return tab;
	}

	static BufferedImage hotbarTabSheet(BufferedImage gui) {
		BufferedImage sheet = new BufferedImage(12, 22, BufferedImage.TYPE_INT_ARGB);
		BufferedImage tab = hotbarTab(gui, -1);
		int lit = gui.getRGB(1, 30);
		for (int y = 0; y < 22; y++) {
			for (int x = 0; x < 6; x++) {
				sheet.setRGB(x, y, tab.getRGB(x, y));
			}
		}
		for (int pip = 0; pip < 4; pip++) {
			for (int y = 2 + 5 * pip; y <= 4 + 5 * pip; y++) {
				sheet.setRGB(8, y, lit);
				sheet.setRGB(9, y, lit);
				sheet.setRGB(10, y, lit);
			}
		}
		return sheet;
	}

	static BufferedImage withBtaSprites(BufferedImage cypress, BufferedImage bta, int fromX) {
		BufferedImage out = region(cypress, 0, 0, cypress.getWidth(), cypress.getHeight());
		int w = Math.min(bta.getWidth(), out.getWidth());
		int h = Math.min(bta.getHeight(), out.getHeight());
		for (int y = 0; y < h; y++) {
			for (int x = fromX; x < w; x++) {
				int pixel = bta.getRGB(x, y);
				if ((pixel >>> 24) != 0) {
					out.setRGB(x, y, pixel);
				}
			}
		}
		return out;
	}

	static BufferedImage creative(BufferedImage inventory, BufferedImage btaCreative) {
		int panel = inventory.getRGB(100, 20);
		int frame = inventory.getRGB(0, 40);
		int well = inventory.getRGB(20, 40);
		BufferedImage out = new BufferedImage(btaCreative.getWidth(), btaCreative.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < 166; y++) {
			for (int x = 0; x < 176; x++) {
				out.setRGB(x, y, inventory.getRGB(x, y));
			}
		}
		box(out, 176, 0, 122, 166, frame, panel);
		box(out, 182, 6, 98, 20, frame, 0xFF000000);
		box(out, 182, 28, 110, 110, frame, well);
		return out;
	}

	static BufferedImage recolour(BufferedImage bta, int panel, int frame, int well) {
		int w = bta.getWidth();
		int h = bta.getHeight();
		BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int pixel = bta.getRGB(x, y);
				if ((pixel >>> 24) == 0xFF) {
					switch (pixel & 0xFFFFFF) {
						case 0xC6C6C6, 0x555555 -> pixel = panel;
						case 0xFFFFFF, 0x373737 -> pixel = frame;
						case 0x8B8B8B -> pixel = well;
						case 0x000000 -> {
							if (bordersTransparency(bta, x, y)) {
								pixel = frame;
							}
						}
						default -> {
						}
					}
				}
				out.setRGB(x, y, pixel);
			}
		}
		return out;
	}

	static BufferedImage taperGlow(BufferedImage source) {
		return taperGlow(source, GLOW_INNER);
	}

	static BufferedImage taperGlow(BufferedImage source, double inner) {
		int w = source.getWidth();
		int h = source.getHeight();
		double cx = w / 2.0;
		double cy = h / 2.0;
		double radius = Math.min(w, h) / 2.0;
		BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				double dx = (x + 0.5 - cx) / radius;
				double dy = (y + 0.5 - cy) / radius;
				double distance = Math.sqrt(dx * dx + dy * dy);
				double fade;
				if (distance <= GLOW_INNER) {
					fade = 1.0;
				} else if (distance >= 1.0) {
					fade = 0.0;
				} else {
					double t = 1.0 - (distance - GLOW_INNER) / (1.0 - GLOW_INNER);
					fade = t * t * (3.0 - 2.0 * t);
				}
				int pixel = source.getRGB(x, y);
				int r = (int) Math.round(((pixel >> 16) & 0xFF) * fade);
				int g = (int) Math.round(((pixel >> 8) & 0xFF) * fade);
				int b = (int) Math.round((pixel & 0xFF) * fade);
				out.setRGB(x, y, (pixel & 0xFF000000) | (r << 16) | (g << 8) | b);
			}
		}
		return out;
	}

	private static void box(BufferedImage image, int x, int y, int w, int h, int frame, int fill) {
		for (int yy = y; yy < y + h; yy++) {
			for (int xx = x; xx < x + w; xx++) {
				boolean edge = xx == x || xx == x + w - 1 || yy == y || yy == y + h - 1;
				image.setRGB(xx, yy, edge ? frame : fill);
			}
		}
	}

	private static boolean bordersTransparency(BufferedImage image, int x, int y) {
		int[][] offsets = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
		for (int[] offset : offsets) {
			int nx = x + offset[0];
			int ny = y + offset[1];
			if (nx < 0 || ny < 0 || nx >= image.getWidth() || ny >= image.getHeight() || (image.getRGB(nx, ny) >>> 24) == 0) {
				return true;
			}
		}
		return false;
	}

	private static int difference(int a, int b) {
		int total = 0;
		for (int shift = 0; shift < 32; shift += 8) {
			total += Math.abs(((a >>> shift) & 0xFF) - ((b >>> shift) & 0xFF));
		}
		return total;
	}
}
