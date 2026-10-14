# 流畅的无线 CarPlay / Smooth wireless CarPlay

Wi-Fi 信道与画面尺寸都会影响无线 CarPlay 的流畅度感受。下面的数值是在一辆 2024 款 BYD 唐（DiLink 5.0，2560×1440 屏幕）上，配合运行 iOS 27 的 iPhone、停车状态下滚动同一份 Apple Music 列表测得的。其他车型、固件和手机的表现可能不同；干扰、扫描和解码器限制也可能产生影响。

The Wi-Fi channel and the size of the picture can affect how smooth wireless CarPlay feels. The numbers below were measured on a 2024 BYD Tang (DiLink 5.0, 2560×1440 screen) with an iPhone on iOS 27, parked, while scrolling the same Apple Music list. Other cars, firmware and phones may behave differently; interference, scanning and decoder limits can also contribute.

本指南描述 0.2.13 的信道策略与旋转行为。Wi-Fi Direct 在 Android 9 及以上且固件合适时可用。Android 9 使用旧版 group/channel 路径，无法核实协商出的频率；参见 [Android 9 的限制](ANDROID9_WIFI_DIRECT.md)。Android 10+ 保留了实际频率校验。下面贡献者测得的数据描述的是其所述的唐这套配置，并不代表所有设备。

This guide describes 0.2.13 channel-policy and rotation behavior. Wi-Fi Direct is available on Android 9+ with suitable firmware. Android 9 uses the legacy group/channel path and cannot verify the negotiated frequency; see [Android 9 limits](ANDROID9_WIFI_DIRECT.md). Android 10+ retains actual-frequency verification. The contributor measurements below describe their specified Tang setup, not every device.

**如果车机自带热点，请使用车机热点。** Wi-Fi Direct 需要打开车机的 Wi-Fi 开关，而当车机没有加入任何网络时它会去搜索网络——根据 DiLink 4.0 与 5.0 车型的报告，搜索间隔为每 10 秒一次。每次搜索都会把车机仅有的那颗无线电从 CarPlay 信道上拉走；此间发出的音频包会丢失，因此无论你选哪个信道，音乐与导航语音都会按这个节奏卡顿，加大音乐缓冲也救不回来。使用车机热点时，车机的 Wi-Fi client 是关闭的：在同样的车上，这样几乎没有丢失音频。在网络 ADB 已授权的情况下，DiPlay 会在 Android 7.1 及更高版本的 Wi-Fi Direct 会话期间暂停这种搜索；参见 [Compatibility](COMPATIBILITY.md)。

**Use the built-in car hotspot if your car has one.** Wi-Fi Direct needs the car's Wi-Fi switch on, and while the car is not joined to a network it searches for one, every 10 s in reports from DiLink 4.0 and 5.0 cars. Each search takes the car's single radio off the CarPlay channel; audio packets sent meanwhile are lost, so music and navigation voice stutter on that rhythm whatever channel you choose, and a larger music buffer cannot bring them back. The car hotspot runs with the car's Wi-Fi client off: on the same cars it lost no audio, or almost none. With network ADB already approved, DiPlay pauses this search during Wi-Fi Direct sessions on Android 7.1 and later; see [Compatibility](COMPATIBILITY.md).

## 1. Wi-Fi 信道 / 1. Wi-Fi channel

**Settings → Connection setup → Wi-Fi Direct → Preferred channel**

在这辆唐上，让 Wi-Fi Direct 运行在与车机所加入的 Wi-Fi 网络不同的 5 GHz 信道上，会导致：

On this Tang, running Wi-Fi Direct on a different 5 GHz channel from the car's joined Wi-Fi network caused:

- 画面一次停顿 0.6–2 秒；
- 音乐卡顿。

- the picture stopping for 0.6–2 s at a time;
- music stuttering.

在本次测试中，与 5 GHz 网络并存的 2.4 GHz Wi-Fi Direct 信道则很流畅。该结果与无线电信道争用的解释一致，但不能证明每台车机都具备相同的射频能力或存在相同成因。

A 2.4 GHz Wi-Fi Direct channel alongside the 5 GHz network was smooth in this test. The result is consistent with radio channel contention, but does not establish the same radio capabilities or cause on every head unit.

- **Auto**（默认）可以复用兼容的已保存配置。在已建立的 5 GHz station 连接旁边，它会优先选用符合条件的已保存频率或所支持的、与 station 相符的信道，然后先尝试明确指定的 2.4 GHz，再尝试其他 5 GHz。在这种情况下，一条未固定（unpinned）的已保存 system-default 请求会被推迟。如果明确尝试被拒绝，后续仍可能回退到其他 5 GHz 或 system-default；Auto 无法保证某个频段，也无法消除争用。在没有 station 连接时，兼容的已保存配置仍可能先于通常的"5 GHz 优先"回退顺序被尝试。Android 10+ 可在诊断报告中查看已核实的频率，Android 9 则可查看明确标注为未经核实的请求信道。
- 如果你手动指定了信道而车机加入的是 5 GHz 网络，可以改试受支持的 2.4 GHz 信道（1–11），或者改回 Auto。固件与当地法规限制依然适用。

- **Auto** (the default) can reuse a compatible saved configuration. Beside an established 5 GHz station connection, it prioritizes an eligible saved frequency or a supported matching station channel, followed by explicit 2.4 GHz attempts before other 5 GHz attempts. An unpinned saved system-default request is deferred in that case. If explicit attempts are rejected, later 5 GHz or system-default fallbacks remain possible; Auto cannot guarantee a band or eliminate contention. Without a station connection, a compatible saved configuration may still be tried before the usual 5 GHz-first fallback order. See the diagnostic report for the verified frequency on Android 10+, or the explicitly unverified requested channel on Android 9.
- If you choose a channel by hand and the car joins a 5 GHz network, try a supported 2.4 GHz channel (1–11), or go back to Auto. Firmware and local regulatory limits still apply.

## 2. 画面尺寸与屏幕旋转 / 2. Picture size and screen rotation

**Settings → Display and performance → Turn CarPlay with the screen without reconnecting**

画面越大，编码、传输和解码的工作量都可能增加。在这次测试中，三种情况下 DiPlay 接收帧率与渲染帧率都很接近，而更大的方形画面到达帧率更低。这提示可以尝试调整画面尺寸，但其本身并不能据此断定 iPhone 编码器就是瓶颈，也无法排除网络影响与背压。

Larger pictures can increase encoding, transmission and decoding work. In this test, DiPlay's received and rendered frame rates were close in all three cases, while the larger square arrived at a lower rate. This suggests testing picture size, but does not by itself identify the iPhone encoder as the bottleneck or rule out network effects and backpressure.

| 设置 | 画面 | 每帧像素数 | 滚动时的帧率 |
|---|---|---|---|
| 旋转**关闭** | 2560×1440，屏幕本身尺寸 | 370 万 | 45–57 |
| 旋转开启，**Smoother (1920)** | 1920×1920 方形 | 370 万 | 49–57 |
| 旋转开启，**Sharper（屏幕尺寸）** | 2560×2560 方形 | 660 万 | 32–44 |

| Setting | Picture | Pixels per frame | Frames per second while scrolling |
|---|---|---|---|
| Rotation **off** | 2560×1440, the screen's own size | 3.7 million | 45–57 |
| Rotation on, **Smoother (1920)** | 1920×1920 square | 3.7 million | 49–57 |
| Rotation on, **Sharper (the screen's size)** | 2560×2560 square | 6.6 million | 32–44 |

开启旋转后，CarPlay 拿到的是一幅方形画面，横屏和竖屏都能容纳其中。此时旋转屏幕会在不重连的情况下重新绘制 CarPlay。1920 的方形与 2560×1440 屏幕的像素数完全相同，在这里帧率也相近；像素数相同并不保证性能相同。它在该屏幕上会被放大，因此文字可能显得发虚。2560 的方形像素数约为 1.8 倍，本次测试中帧率更低。实际方形尺寸还取决于所选解码器的能力。

With rotation on, CarPlay gets a square picture that holds both a landscape and a portrait screen. Turning the screen then redraws CarPlay without reconnecting. A 1920 square has exactly as many pixels as a 2560×1440 screen and had a similar frame rate here; equal pixel counts do not guarantee equal performance. It is scaled up on this screen, so text can look softer. A 2560 square has about 1.8 times as many pixels and had a lower frame rate in this test. The actual square size also depends on the selected decoder's capabilities.

**该怎么选：**

**What to choose:**

- **你的屏幕从不旋转，或者很少旋转：**试试把旋转保持**关闭**。在这辆唐上这样最流畅，也避免了把较小的方形放大到整屏。如果你确实旋转了屏幕，CarPlay 会按新尺寸重连，在唐上大约需要 10 秒。
- **你经常旋转屏幕：**打开旋转，并保持默认的 **Smoother (1920)**。
- **Sharper（屏幕尺寸）：**在宽于 1920 像素的屏幕上，它可能会请求一个更大的受支持方形，从而可能降低流畅度。在 1920 像素及以下，两个选项的尺寸上限是一样的。

- **Your screen never turns, or you rarely turn it:** try leaving rotation **off**. That felt smoothest on this Tang and avoids scaling a smaller square up to its screen. If you do turn the screen, CarPlay reconnects at the new size, which took about 10 s on the Tang.
- **You turn the screen often:** turn rotation on and keep **Smoother (1920)**, the default.
- **Sharper (the screen's size):** on a screen wider than 1920 pixels it can request a larger supported square and may reduce smoothness. At 1920 pixels or below, both options have the same size limit.

## 3. 流畅视频（实验性）/ 3. Smooth video (experimental)

**Settings → Display and performance → Smooth video (experimental)**，默认关闭。

**Settings → Display and performance → Smooth video (experimental)**, off by default.

默认情况下，DiPlay 一收到解码器释放的帧就立即显示。在这辆唐上，解码器的输出时序取决于之后的输入：

By default DiPlay shows each frame as soon as the decoder releases it. On the Tang, the decoder's output timing depended on later input:

- **解码器会压帧。**`c2.qti.avc.decoder` 要等到又排入约两帧之后才释放一帧。该设置关闭时（下文 A1、A2 两次运行，约 56 fps），入队到输出的耗时（`decode p50/p90`）在多数 5 秒窗口内为 48–56 ms（中位数）和 69–77 ms（p90），而在静止画面上，最后一帧要等到下一帧一起才出来。
- **iPhone 的码流并没有提出这种要求。**它的 SPS 声明 `max_num_reorder_frames` 为 0，且帧中没有 B slice。
- **没有任何解码器设置改变过这一点。**尝试过的有：
  - `KEY_LOW_LATENCY`（解码器并未宣称支持）；
  - `vendor.qti-ext-dec-picture-order.enable`；
  - `vendor.qti-ext-dec-timestamp-reorder.value` 为 0；
  - SPS 中的 Constrained High 标志。

- **The decoder held frames.** `c2.qti.avc.decoder` released a frame only after about two more had been queued. With the setting off (runs A1 and A2 below, about 56 fps), queue-to-output time (`decode p50/p90`) was 48–56 ms (median) and 69–77 ms (p90) in most 5 s windows, and on a still screen the last frame came out only with the next one.
- **The iPhone's stream does not ask for that.** Its SPS signals `max_num_reorder_frames` 0, and the frames had no B slices.
- **No decoder setting changed it.** These were tried:
  - `KEY_LOW_LATENCY` (the decoder does not advertise it);
  - `vendor.qti-ext-dec-picture-order.enable`;
  - `vendor.qti-ext-dec-timestamp-reorder.value` 0;
  - Constrained High flags in the SPS.

iPhone 在屏幕头中为每一帧标注了自己的时间。在这辆唐上以 60 fps 运行时，这些时间落在 1/60 秒的栅格上，iPhone 跳过的帧留下的空缺都是整数倍。启用该设置后，DiPlay：

The iPhone stamps each frame with its own time in the screen header. On the Tang at 60 fps these times fell on a 1/60 s grid, and frames the iPhone skipped left gaps of whole multiples. With this setting, DiPlay:

- 把主屏渲染到一个 `SurfaceView`；
- 将该时间映射到车机的时钟上，并加上链路的基础延迟：近期到达时间（帧时间到到达）的一个低分位数。前 30 帧中，基础值立即跟随该分位数。之后每秒最多移动 2 ms，除非它上升超过 0.5 秒或下降超过 100 ms，此时直接跳到新值；
- 在该本地时间加上一个显示延迟的时刻，用 `releaseOutputBuffer(index, timestampNs)` 释放每一帧。

- renders the main screen to a `SurfaceView`;
- maps that time onto the head unit's clock, adding the link's base delay: a low percentile of recent arrivals (frame time to arrival). For the first 30 frames the base follows that percentile at once. After that it moves at most 2 ms per second, unless it rises by more than 0.5 s or falls by more than 100 ms, when it jumps to the new value;
- releases each frame with `releaseOutputBuffer(index, timestampNs)` at that local time plus a display delay.

显示延迟会自我调整。对于每一帧能计时的帧，DiPlay 记录解码器在它的本地时间之后多久才释放它。那些只是在 iPhone 帧出现停顿之后才释放的帧不计入：即相邻 iPhone 帧时间间隔超过 120 ms 的空缺之前的最后三帧（因为解码器大约压两帧），以及在超过 0.5 秒静止画面之前排入的帧。每累计 15 个可计数的帧，延迟的目标值就设为最近 120 个可计数帧的第 90 百分位数再加 20 ms 余量，并保持在 30 到 200 ms 之间，目标是让大约九成的帧按时就绪。余量取一个刷新周期加 4 ms，因为 SurfaceFlinger 大约会在该帧呈现的那个 vsync 之前一个刷新周期取走缓冲区（下文有实测）。它每帧最多上升 1 ms、最多下降 0.5 ms，因此一次变化会摊到多帧上，而不会让之后每一帧立刻移位。初始值为帧率设置的三个帧间隔再加 40 ms：60 fps 时为 90 ms，30 fps 时为 140 ms。在自己的时间之后才离开解码器的帧会立即显示并计为 `late`，统计行会显示当前的 `delay`。

The display delay adjusts itself. For each frame it can time, DiPlay notes how long after its local time the decoder released it. Frames released only after a pause in the iPhone's frames are not counted: the last three frames before a gap of more than 120 ms between consecutive iPhone frame times, since the decoder holds about two, and frames queued before a still screen of more than 0.5 s. Every 15 counted frames, the delay's goal is set to the 90th percentile of the last 120 counted frames plus a 20 ms margin, kept between 30 and 200 ms, aiming for about nine in ten frames ready in time. The margin is a refresh plus 4 ms because SurfaceFlinger takes a buffer about one refresh before the vsync it is shown at (measured below). It rises by at most 1 ms per frame and falls by at most 0.5 ms per frame, so a change spreads over many frames instead of shifting every later frame at once. It starts at three frame intervals of the frame-rate setting plus 40 ms: 90 ms at 60 fps, 140 ms at 30 fps. Frames that still leave the decoder after their time are shown at once and counted as `late`, and the stats line shows the current `delay`.

**在我的唐上以固定 90 ms 延迟实测**（当时是本改动的一个更早版本，在现有的 SurfaceView 出现之前自行搭建了 `SurfaceView`，且延迟尚未自适应；USB，2560×1440 @ 60 fps，滚动进出时交替采集关闭/开启各 41–53 秒，滚动时 `rx` 约 56 fps）。间隔数据来自视频图层的 `dumpsys SurfaceFlinger --latency`（关闭时为应用窗口，开启时为 `SurfaceView`），只统计至少有 40 个已呈现帧的秒：

**Measured on my Tang with a fixed 90 ms delay**, with an earlier version of this change that set up its own `SurfaceView` before the current one existed, and before the delay adjusted itself (USB, 2560×1440 at 60 fps, alternating off/on captures of 41–53 s while scrolling on and off, `rx` about 56 fps while scrolling). Intervals come from `dumpsys SurfaceFlinger --latency` for the video layer (the app window when off, the `SurfaceView` when on), counting only seconds with at least 40 presented frames:

| 运行 | 流畅视频 | 计入秒数 | 下一帧晚 1 个刷新 | 晚 2 个刷新 | 晚 3 个及以上 | 呈现 fps |
|---|---|---|---|---|---|---|
| A1 | 关 | 9 | 63.6% | 32.5% | 3.9% | 42.4 |
| B1 | 开 | 29 | 86.0% | 12.4% | 1.5% | 51.2 |
| A2 | 关 | 31 | 63.8% | 31.4% | 4.8% | 42.1 |
| B2 | 开 | 37 | 83.4% | 14.2% | 2.5% | 50.0 |

| Run | Smooth video | Seconds counted | Next frame 1 refresh later | 2 refreshes later | 3 or more | Presented fps |
|---|---|---|---|---|---|---|
| A1 | off | 9 | 63.6% | 32.5% | 3.9% | 42.4 |
| B1 | on | 29 | 86.0% | 12.4% | 1.5% | 51.2 |
| A2 | off | 31 | 63.8% | 31.4% | 4.8% | 42.1 |
| B2 | on | 37 | 83.4% | 14.2% | 2.5% | 50.0 |

- 打开该设置后，更多收到的帧抵达了屏幕，且更多帧之间恰好相隔一个刷新周期。这与"解码器成簇的输出被重新铺回 iPhone 的栅格"这一解释一致；`SurfaceView` 本身贡献了多少并没有单独测量。更早一次使用 `SurfaceView` 且无节流的 USB 运行，按同样方式统计（8 秒），一个刷新周期的比例为 63.4%，与关闭时的运行接近。
- 在固定 90 ms 的情况下，在开启运行中 `rx` 高于 53 fps 的那些 5 秒窗口里，`late` 为 36–75（约占收到帧的 13–26%），也就是说仍有一部分帧在自己的时间之后才离开解码器。
- 在该设置为 60 fps 时，主屏有时会长达一分钟以上稳定在 30 fps。B2 运行刚结束时通过 USB 采集（无触摸操作），`rx` 为 29–34 fps，`late` 为每 5 秒 49–112（约占收到帧的 31–75%）。在之后一次无线会话中（使用车机热点，我在这期间切换 CarPlay 应用，其中包括地图），`late` 为每 5 秒 98–128（约 62–85%）。也就是说在这些时段里，大多数帧一离开解码器就被释放，与设置关闭时相同；当时到达屏幕的情况没有被采集下来。这就是延迟如今会自我调整的原因。
- 当 DiPlay 退到后台时，主屏解码器会移到一块离屏 surface 并保留其状态，返回时 DiPlay 会向 iPhone 请求新的关键帧。在车上画面立即恢复，只短暂闪了一下。

- With the setting on, more of the received frames reached the screen and more of them came one refresh apart. This is consistent with the decoder's bunched output being spread back onto the iPhone's grid; it was not measured separately how much the `SurfaceView` alone contributes. An earlier USB run with a `SurfaceView` and no pacing, counted the same way (8 seconds), gave 63.4% at one refresh, close to the off runs.
- With the fixed 90 ms, in the 5 s windows of the on runs where `rx` was above 53 fps, `late` was 36–75 (about 13–26% of the frames received), so a share of frames still left the decoder after their time.
- The main screen sometimes arrived at a steady 30 fps for over a minute while the setting was 60 fps. Over USB right after run B2 (no touches), `rx` was 29–34 fps and `late` was 49–112 per 5 s (about 31–75% of the frames received). In a later wireless session (car hotspot, while I switched between CarPlay apps, the map among them), `late` was 98–128 per 5 s (about 62–85%). So for those stretches most frames were released as soon as they left the decoder, as with the setting off; what reached the screen was not captured then. This is why the delay now adjusts itself.
- When DiPlay goes to the background, the main-screen decoder moves to an offscreen surface and keeps its state, and on return DiPlay asks the iPhone for a new keyframe. In the car the picture came back at once, with a short blink.

**在我的唐上以自适应延迟和 4 ms 余量实测**（自适应延迟的首个版本，车机热点，2560×1440 @ 60 fps，一次约 3 分钟的会话：列表、地图、再列表、切后台再回来；不是 A/B 运行）：

**Measured on my Tang with the adjusting delay and a 4 ms margin**, the first version of the adjusting delay (car hotspot, 2560×1440 at 60 fps, one session of about 3 minutes: lists, the map, lists again, background and back; not an A/B run):

- `dumpsys SurfaceFlinger --latency` 中的 `SurfaceView` 图层，至少有 40 个已呈现帧的秒（共 108 秒，几乎全是列表界面）：88.7% 的帧在 1 个刷新周期后出现下一帧，9.1% 晚 2 个刷新周期，1.7% 晚 3 个及以上；呈现 51.2 fps。
- 主屏每 5 秒窗口：

- `SurfaceView` layer in `dumpsys SurfaceFlinger --latency`, seconds with at least 40 presented frames (108 s, nearly all lists): the next frame came 1 refresh later for 88.7% of frames, 2 refreshes later for 9.1%, and 3 or more for 1.7%; 51.2 presented fps.
- Per 5 s window of the main screen:

| 内容 | 窗口数 | `rx` | `delay` | `late`，占收到帧的比例 |
|---|---|---|---|---|
| 列表 | 21 | 43–60 fps | 76–116 ms，其中一个窗口 190 ms | 中位数 8%，18 个窗口为 3–12%，其余为 15%、20% 和 35% |
| 地图 | 9 | 25–41 fps | 97–155 ms | 中位数 16%，10–34% |

| Content | Windows | `rx` | `delay` | `late`, share of frames received |
|---|---|---|---|---|
| Lists | 21 | 43–60 fps | 76–116 ms, one window 190 ms | median 8%, 18 windows 3–12%, the others 15%, 20% and 35% |
| Map | 9 | 25–41 fps | 97–155 ms | median 16%, 10–34% |

- 在地图上 iPhone 是成突发地发帧的，间隔最长约 0.5 秒（每个窗口内 `maxGap` 为 468–488 ms），因此"一个刷新周期"那一项的比例并不能描述它。
- 那个 190 ms 的窗口紧跟在我切换应用时约一秒的静止画面之后；它同时也是 `late` 为 35% 的那个窗口。
- 该构建仍把 iPhone 帧停顿期间被压住的帧计为 `late`。本次改动的构建会把它们排除，因此在相同画面下它的 `late` 读数会更低。
- 这次会话使用的是车机热点，而固定延迟那张表用的是 USB，且是不同的运行，两者并非同条件对比。

- On the map the iPhone sent frames in bursts, with gaps of up to about 0.5 s (`maxGap` 468–488 ms in every window), so the share at one refresh does not describe it.
- The 190 ms window followed a still screen of about a second while I switched apps; it is also the window with 35% `late`.
- This build still counted frames held over a pause in the iPhone's frames as `late`. The build in this change leaves them out, so its `late` reads lower for the same picture.
- This session used the car hotspot and the fixed-delay table used USB, on a different run, so the two are not a like-for-like comparison.

**余量，在我的唐上实测**（车机热点，2560×1440 @ 60 fps；每次运行一个连接，每次滚动约 1–1.5 分钟，前五次还包含地图；运行顺序为 4、12、20、4、20 ms，随后还有两次 20 ms 且不含地图）。对每次运行，应用释放的每一帧都按时间戳与 SurfaceFlinger 的记录做了匹配，因此 SurfaceFlinger 丢弃的帧也被计入。只统计至少有 40 个已呈现帧的秒；对同一份数据的两次独立分析，在这些数字上的差异约为 1 个百分点以内：

**Margin, measured on my Tang** (car hotspot, 2560×1440 at 60 fps; one connection per run, about 1–1.5 minutes of scrolling each, plus the map in the first five; runs in the order 4, 12, 20, 4, 20 ms, then two more at 20 ms without the map). For each run, every frame the app released was matched to SurfaceFlinger's record by its timestamp, so frames SurfaceFlinger dropped are counted too. Seconds with at least 40 presented frames only; two separate analyses of the same data agreed within about 1 percentage point on these figures:

| 余量 | 运行次数 | 下一帧晚 1 个刷新 | `late` | 错过其刷新周期：迟到呈现、晚一个刷新周期呈现，或被丢弃 | 延迟，中位数 |
|---|---|---|---|---|---|
| 4 ms | 2 | 88.7–89.2% | 6.8–7.4% | 15–18% | 86–92 ms |
| 12 ms | 1 | 92.6% | 4.0–4.2% | 8–9% | 98–99 ms |
| 20 ms | 4 | 90.5–94.5% | 1.9–3.3% | 5–8% | 108–113 ms |

| Margin | Runs | Next frame 1 refresh later | `late` | Missed their refresh: late, presented a refresh late, or dropped | Delay, median |
|---|---|---|---|---|---|
| 4 ms | 2 | 88.7–89.2% | 6.8–7.4% | 15–18% | 86–92 ms |
| 12 ms | 1 | 92.6% | 4.0–4.2% | 8–9% | 98–99 ms |
| 20 ms | 4 | 90.5–94.5% | 1.9–3.3% | 5–8% | 108–113 ms |

- 每一帧即使按时释放，只要在目标 vsync 之前不足约 15.75 ms，就会错过该 vsync；在提前 16–16.5 ms 释放时，大约有四分之一到三分之一的帧会错过；从约 17 ms 起，错过率为 1–6%。这与"SurfaceFlinger 提前一个刷新周期取走缓冲区"的结论一致，也正是 4 ms 余量导致如此多帧不够用的原因。
- 12 ms 余量只有一次很短的运行，因此尚不清楚它在该屏幕上是否与 20 ms 有差别；在每一次运行中 20 ms 错过的帧都更少。
- 两次分析把错过的帧在"迟到呈现"与"被丢弃"之间的划分方式不同，因此只给出两者之和。

- Every frame released on time but less than about 15.75 ms before the vsync after its target missed that vsync; at 16–16.5 ms before it, about a quarter to a third did; from about 17 ms on, 1–6%. That is consistent with SurfaceFlinger taking the buffer one refresh ahead, and it is why a 4 ms margin left so many frames short.
- The 12 ms margin has only one short run, so it is not clear whether it differs from 20 ms on the screen; 20 ms missed fewer frames in every run.
- The two analyses split the missed frames between "presented late" and "dropped" differently, so only their sum is given.

**代价与限制：**

**Cost and limits:**

- 每一帧都会被压到它的显示时刻，最多约为到达之后的一个延迟，因此触摸响应会变慢。增加的"触摸到屏幕"延迟没有经过测量。
- 画面调节不会生效，因为视频不再经过应用这些调节的视图。
- 这些运行都是在同一辆车上、停车状态、使用同一款解码器完成的，没有尝试其他解码器。

- Each frame is held until its display time, up to about the delay after it arrives, so touches respond later. The added touch-to-screen delay was not measured.
- Picture adjustments do not apply, because the video no longer passes through the view they are applied to.
- These runs were made on one car, parked, with one decoder. Other decoders were not tried.

## 4. 自行检查 / 4. Checking it yourself

CarPlay 运行期间，DiPlay 每 5 秒记录一行 `DiPlay-VideoStats`：

While CarPlay runs, DiPlay logs a `DiPlay-VideoStats` line every 5 s:

```
video stats rx=56.2fps shown=56.4fps maxGap=73ms kbps=28899 ...
```

- `rx` 统计 DiPlay 收到的帧；`shown` 统计被释放去渲染到其 surface 的解码器输出。它们是窗口计数器，并不能证明手机发出的每一帧或每一次物理显示刷新。
- 如果 `shown` 跟得上 `rx`，但滚动时 `rx` 偏低，可以对比画面设置与 Wi-Fi 链路。仅凭计数器无法区分是手机编码、传输延迟还是背压，也不能排除所有车机端的问题。
- `decode p50/p90` 是从一帧排入解码器到取出其输出的时间。在超过 0.5 秒的输入空缺（静止画面）之前排入的帧，会被同时排除在 `decode p50/p90` 和 `late` 之外。开启流畅视频后，`late` 统计的是因为已过显示时间才立即显示、或根本没有可用显示时间的帧；iPhone 帧停顿期间被压住的帧也被排除，因为任何延迟都不可能遮住它们。`delay` 是最后一个带节流帧处的显示延迟。
- `maxGap` 是收到帧之间小于 2 秒的最大间隔；更长的间隔被排除。静止画面也可能产生间隔，因为 iPhone 无需发送新帧。单个较大数值本身并不能作为卡顿的证据。

- `rx` counts frames received by DiPlay; `shown` counts decoder outputs released for rendering to its surface. These are windowed counters, not proof of every frame the phone sent or every physical display refresh.
- If `shown` keeps up with `rx` but `rx` is low while you scroll, compare picture settings and the Wi-Fi link. The counters alone cannot distinguish phone encoding, transport delays or backpressure, and do not rule out every head-unit problem.
- `decode p50/p90` is the time from queueing a frame into the decoder to dequeueing its output. Frames queued before an input gap of more than 0.5 s (a still screen) are left out of both `decode p50/p90` and `late`. With smooth video on, `late` counts frames shown at once because they left the decoder after their display time, or had no usable display time; frames held over a pause in the iPhone's frames are left out too, since no delay could have hidden them. `delay` is the display delay at the last paced frame.
- `maxGap` is the largest interval between received frames shorter than 2 s; longer intervals are excluded. A static screen may also produce gaps because the iPhone need not send new frames. A single large value is not evidence of a stall by itself.
