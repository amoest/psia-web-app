"""Buat sumber ikon & splash dari crest PS IA-ITB (assets/source/assets_img_psia-crest.png).
Jalankan: python3 scripts/make-icons.py && npm run assets"""
from PIL import Image

BG = (7, 12, 24, 255)  # #070C18, warna tema situs
crest = Image.open("assets/source/assets_img_psia-crest.png").convert("RGBA")
crest = crest.crop(crest.getbbox())

def place(canvas_size, height_ratio, bg):
    canvas = Image.new("RGBA", (canvas_size, canvas_size), bg)
    h = int(canvas_size * height_ratio)
    w = int(crest.width * h / crest.height)
    logo = crest.resize((w, h), Image.LANCZOS)
    canvas.alpha_composite(logo, ((canvas_size - w) // 2, (canvas_size - h) // 2))
    return canvas

# Ikon legacy (persegi penuh), crest ~74% tinggi
place(1024, 0.74, BG).save("assets/icon-only.png")
# Adaptive foreground: template Capacitor memberi inset 16.7%, jadi gambar = area terlihat; tinggi 76% aman untuk mask bulat
place(1024, 0.76, (0, 0, 0, 0)).save("assets/icon-foreground.png")
Image.new("RGBA", (1024, 1024), BG).save("assets/icon-background.png")
# Splash
for name in ("splash.png", "splash-dark.png"):
    place(2732, 0.22, BG).save("assets/" + name)
print("ok")
