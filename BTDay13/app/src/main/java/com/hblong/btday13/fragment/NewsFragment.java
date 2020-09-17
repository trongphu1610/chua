package com.hblong.btday13.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hblong.btday13.R;
import com.hblong.btday13.adapter.PageAdapter;
import com.hblong.btday13.model.Page;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {
    private RecyclerView rscv;
    private PageAdapter pagerAdapter;
    private List<Page> pages;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        rscv = (RecyclerView) view.findViewById(R.id.rscv);

        pages = new ArrayList<>();
        pages.add(new Page("Zing News", "https://freelancervietnam.vn/wp-content/uploads/2019/04/t%E1%BA%A3i-xu%E1%BB%91ng-1.jpg"));
        pages.add(new Page("Vnexpress", "https://freelancervietnam.vn/wp-content/uploads/2019/04/VnExpress_logo.png"));
        pages.add(new Page("Tin Tức 24H", "data:image/png;base64,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"));
        pages.add(new Page("Việt Nam net", "data:image/jpeg;base64,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"));
        pages.add(new Page("Báo Tuổi Trẻ", "https://static.mediacdn.vn/tuoitre/web_images/LogoTTTV.png"));
        pages.add(new Page("Kênh 14", "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBxAREhMSEBIVFRUQFhAWFRYPFRUVFRUQFRUWFhUVFhUYHSggGBolHRUXITEhJSkrLi4uFx8zODMtNygtLisBCgoKDg0OGxAQGy0lICAtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tKy0tLf/AABEIAOEA4QMBEQACEQEDEQH/xAAcAAACAgMBAQAAAAAAAAAAAAAABgEHAwQFAgj/xABOEAABAwICAwkMBwQJBAMAAAABAAIDBBEFIQYSMRNBUWFxcoGRsQcUIjIzNFJTVJPB0RVCc5Khs+EWFyNiJCU1Q3SCg6LCVZSy40TS8f/EABoBAQACAwEAAAAAAAAAAAAAAAAEBQECAwb/xAA0EQACAQMBBgUEAAYCAwAAAAAAAQIDBBESBRMhMTJRFDNBUnEiNGGBFSNCYnKxodEkkcH/2gAMAwEAAhEDEQA/ALxQAgBACAEAIAQAgBACAEAIAQAgBACAEAIAQAgBACAEAIAQAgBACAEAIAQAgBARdYAaw4UyMMNYcKZQww1hwpkYZGsmQTdARrJlDiGsOFMoYYaw4UyhhhdYyhgm6zlIEayxqQwF01LuMBrBZ1IYC6ZQC6NoE3WNSAXTUgF01IBdMpgkLYAgBACAEAIAQAgBAIndcrZYqWMRvczdJdV2obEt1SbXGdrhRbuTjFYJ1hCMpvJUXfk3rZPeP+artcu5b7uHZB35L62T3j/mmuXcbuHZB35L62T3j/mmuXcbuHZEjEJ25tmlBGYIkfketZVSS45G6g1xSLX04xCbvKkIe4GYMLy02LjuYdtHGpF7UlGnHD5kHZ1GEq0k1yEDvmT1j/vu+aq1Ul3Ze7qn7UQaqT1j/vu+aOpLuw6VP2oO+pPWP++75rG8l3f/ALM7in7Uem10zTrNleC3MEPdketbbya4pmroUmuMUPWlNZIZIxrutuMTrA28JwNzksbTrVFUUU/REHZlCm4SbXqzi7u/03feKrN9U9zLLcU/ag3Z/pu+8U31T3Mbin7UG7v9J33im+qe5jcU/ajo6PVUgqIrPdZzgCCSQQd6xUuyr1N/FanxId/Qp7iT08hmx+V2621jYAWANtu1StqVpqtpTKqypxdPODm7o7hPWVW76p3ZM3cewbo7hPWU31Tuxu49g3R3Cespvandjdx7G3hMzhKwaxzNjntCmWFapv0ska6px3beBtC9UUxKAEAIAQAgBACAEBXvdl82g+2/4OUO86UWGzutlWYbh81Q8RQML3kEhoIGQ25kgKBGLk8ItpzjBZkzZxXAKulDXVMJjDzZpc5hubXt4JKzOnKC4o0p1qdR/Sz1hejdbUs16eBz2A21gWNFxttrEX6FmNKUllIxOvTg8NmHGMEqaW3fMW5l99UFzCTbfs1xyWsoSjwZvTrQqdLLG048xoOaz8oLtfeXEi7M8+Ym0FBLO/UhYXusTYEDIbTmeNV1OnKbxFFxVrQpLM3hDxhOHTUsLWsw90srnNMrpjFbV3wzw8stis6dN04r6MspK1aNao254Xpg5ekWjU75XS09JIxjgXOa8xWa7fLbO2cS4XNtJyzCJKtL2MYaKkuPoKLthUBlrnKHvSfysf2EHYVrtTzl8IhbL8qX+TNenwWpkaHsiLmu2EFufWVHp2VacdUY5TO1S/oQlplLijSkYWkhwsWkgg7xCjuLi8PmiVGaktS5M3n4JUhpeYiGga17t8W177VJdjWUdTXAiK/oOWlPiRgPnMPPasWXnx+TN/8Aby+Bpx/yx5GqVtT7gqLLyzBFh0zgHNYSDsNx81xhZVpx1JcDeV1Ti2mzX1Ds6OlRtPHB21rGTYkw6ZoLiwgAXJuNikzsq0YuTXA5RuacnhMMK8tHypY/cRMXXlMcAvXlGSgBACAEAIAQAgBAV73ZvNoPtv8Ag5Q7zpRYbO62KHcs/tGP7ObsCj2vmE2/8ksjTXRx1eaaO+qxkjnSOG0M1djeMnJTa9PXhFXbV91qZmx7E4sLowY4vBYAyNjBZutbwdYgZDhKzOSpQyka0oSr1MNlG4tiMtTI+aZ2s9+3gAAya0bwHAqyc3Liy+p04wWI8iy9OPMaDms/KC6X3lxIuzPPmc/uaee/6cna1cdn+YStreR+x80nq62MR95xCQku17jYLZb4VncTqRX0LJSWlOjJveywLdZjeMtjeZKZrWBrtY2GTbZnxlClWucPMSfTt7PUsTK7dsVWy/xwHvSfysf2EHYVrtTzl8Ig7L8qX+THPRbzWLkPaVe7O+3j8FDtBf8AkSOJppg//wAiMfaAcG8/5qv2raLzY/v/ALLDZV5j+TL9f9Hdr/M3/Yn/AMVZVftn8FbS+5XyIWAecwc9q8xZefD5PT3/ANvL4GrHh/GPI1S9qfcfoprLyju4X5FnNCvLL7ePwVlfzGcf6Ll1r6u/wjZdU38Pra849Sd4qGnH4OziQ/gv5p7FdXa/kS+CDR8xfIt4YP4zOVebsvuIltcv+UxuC9eUhKAEAIAQAgBACAEBXvdm82g+2/4OUO86UWGzutih3LP7Rj+zm7Ao9r5hNv8Ayf2WnpfpEyggMjhrOcdWNg+s/jO8BtKn1aqprLKi3oOrLCIpZYMUoQXDwKiOzhvsfaxHEWu7EWKkDLUqFTh6MozG8MkpZpIJPGjNr7zmkXa4coVVUi4txZfUqiqw1IsjTjzGg5rPygul95cCLszz5nP7mnnv+nJ2tXLZ/mEravk/sddMMTrIBGaSPXLi7W8AvsAMtmxWFzUqQS0LJTWdKjUb3rwKdbj+LSxvjfTnVkaWm0LwbEZ53UKVe4lFpxLKnbWkJKSnyEpwyKrmXWR70n8rH9hB2Fa7U85fCIOy/Kl/kxy0Y80i5p7Sryw+1j8FFf8A3Mvk0tGMWE7XwS5uZrAX+vHcjrGxcbG6VZSpz5r/AEdb60dFxqQ5P/hnVxhoFNKBsEbgOSymXKxRkvwRLZ5rR+SvcA85g57V5Wy8+Hyeqv8A7efwNeOeWPI1S9qfcfoprPyju4Z5JnNCvLL7ePwVlfzGaX0m+9rDbbfVd/FKmvThczv4ZYybuI+SfzSrK7eaEvg4UfMXyLmG+Wj5V5uy+4iWtz5TG0L15SkoAQAgBACAEAIAQFe92XzaD7b/AIOUO86UWGzut/BXOi2Nd5VDZ9TX1Wvbq3t4wttsodKpolks7ik6sNJ1dMtMjiDI2bjue5uLr62te4twLpWr7xJYOVta7lt55njQ3TJ+HtkZue6MkIcGl2rqvGRI4iLdSxRrummjFzab5pmLTTSdmIajtw3OSO41g7W1oyD4JFt42PWsVqqqehta28qPDPAcNN/MaDms/KCzfeXE57M8+YtaN4x3nNuupr+C5tr222zv0KFb1t1LUWd3bb+GnOBr/eWfZv8Af+im/wAS/tK1bG/uId3SiQR3vtv9f9Ee0c/0hbHx/UIEh29KrJPLbLqK0pIetJ/Kx/YQdhWm1POXwiHsvypf5M3MM0qMMTYtyvqgi+tZdaG1N1TVPTyONfZe9qOpq5nCp6l0cgkYbOa7WHXsPEq6FWUKmuPPJZToxnS0S7YGKs0vMkb4zFbXaRfW4QrSptbXBwceZVUtkaJqWrkzjYB5zDz2qusvPj8lhfv/AMeXwNWOeWPI1TdqfcfoprPyjapMW1WNbq+KLXuutHae7pqGORwqWmqTeTV3TO/Gq7X9Wo76eGDbqsV1mObq7QRe6sau09dNwwR6dq1JSyc7DfLM5VCsvPiSrnymNwXrylJQAgBACAEAIAQEIBN7p2Dz1VMwU7dd0UmsWgjWLdUjK+/motxBzjhEyyqxpzzL1Kt/ZLEvY5vuj5qFuJ9i28VR9yJ/ZLEfY5vuj5p4efYeKo+5B+yWI+xzfdHzWfDz7GPFUfcgbodiTshSS55eEAB0knIJ4efYO7orjqLN0xwGoko6aOJuu6nDA5rdpswNJbfbmu15RlOmlH0INhcwp1nKXJiP+zNd7LL1D5qt8JW9pdeOt/eg/Zmu9ll6h808LW9rHjrf3oP2Zr/ZZeofNPCVvax46396PTNFq9xDe9pBfK7gABxk32LKtKreNJh39uk/qQ5aSYLUOfG5kZeBFGw6m85oN02jZ1pzUoLPBIhbOvaMISjN445OR9B1fqJOpV3gbn2MsfH23vQfQdX6iTqTwNz7GPHW3vQfQlX6iTqTwNx7GPHW3vR0MBwSpE8bnROa1jg4l+QsFKsrGuq0ZSjhIi317QlRlGMstjBjVDK6TWa0uBA2b1lJ2jaVZ1dUFlFXaV4RhpkzTbRTerd1Kv8AB1/ayS7il7j13pN6t3Ung6/tZrv6Xc8uo5vVu6ljwdf2mVXpdzPhlBKJWucwtDTclyl2dnWjVTksYOVxcU3TaTGYFekTyVRKyAQAgBACAEAFAaOL17aeJ0jvqjIcLjkB1qPcVlRpubO1vRdaooL1Ky+lJ9Z7xK4GQlztU2FyvKeLq6nJSfE9grKjpUXFcDn12kVUTZtRJYcDtpUunXrYy5HJ2dunwijRqdJamNpe+plsP5zcngHGu1OpWm8KRpUoW8FlxQqVuleISu1u+Zmj6obI4WHRtKtYZikmyonGMnwSMQx6v9rqPev+ay5s13Uex6OO1/tdR71/zTWzO6j2RH07X+11HvX/ADTWxuo9gdjtffzuo96/5pvGNzHsAx2v9rqPev8AmsbxjdR7HkY9X+11HvX/ADTeM13UewOx+v8Aa6j3r/ms7xjdR7EHHq+3ndR71/zWdbG6j2Ibj1f7XUe9f801sbqPYj6fr/a6j3r/AJrGth0o9g+n6/2uo96/5rOtmN3HsR+0Ff7XUe9f801sbuPYP2hr/a6j3r/mmpjdx7DlotpvUy2hmnfug8V2t44G8f5u1Vt5GrH64SeCfaRoS+mcVkaWYvUAg7q/LhKrfFVU+pk+VlQaxpR3afE5JG33R2e0X3+BSFc1HHqK6drCEsYO3gFZf+G7ezbyb4Vls+vlaJFbeUdL1o7QVqQSUAIAQAgBAQUAgadYlryiFp8GLN32h+Q7V5va9fVPdrkj0uxrbTB1XzYnV8+qLDa7s31W0YanllzN4WDlFwAuTYDhU1Jt4ODaSyxXxKqMzr/Vbk0fE8atqFLdx/JUV6zqSMUUC3bOSSNltMtGzfBkbAmTOkHUtkyNId7ZciZGDyynTJjSeRT5pqGkiSnzTUMBLBsTUY0nncLDlTJnSeBTXWUzDiDoEyYwR3umRggwLOTGDwYSMxcEZgjeKynkxy5D7o3i27s1X+UYAHfzDecPiqW7obuWVyZb2tfeRw+aGPDajUdnsdkfgVFg8G9xT1RyhhhkLXBw2tN1KpzcJKSKqcNUWmOFPKHtDhscAV6enNTipL1KKUXFtMyLc1BACAEAIDBXVAjjfI7YwE9QXOrNQg5P0N6cHOaivUqGWUvc57jm4kk8ZXipyc5uT9T3dOCpwUV6HCqpdZxO9vcim046Y4OcuLOPjM+W5jfzdybwVha0uOplde1sfQjnR0+SmtkBLgWPoR3M6KtooamaScPlDy4RyarcnuaLC2WQUuMI45FXOrNSfE737nMO9bVe+/RZ0R7GN9PuH7nMO9bVe+/RN3HsN9PuB7juHetqvffom7j2G+n3NbE+5TRQQySxy1GtG1zm68t23Gy4tmFrOnHTyN6VaetcSvpKXVcRyquZdLieDT+EsGTyafwkBjlhzN95MPJjgjs6I6C1OKRvnbOKaJriyMui3Qy28Zw8NtgDlv3zU6nQWOJVVbqWr6TvfuXqf+pj/tf/AGrfcQOXiqncj9y1R/1Nv/a/+1Z3MB4qp3FrSHQ+akngpI5xUzVLg1to9z1SSLkjWdcAG5OVrLi6S1YRIjcS3eqRrYxg/e0r4i7W1HObc7+rldcKi0vBKpPVFM5zoFqmbtBRzOhkbI36pzHpN3x0rFSCqQaZmnPRJMf4ZmvaHNzDgCORUE4OEtLLuEtUU0MWGz67BfaMiusHlFbXhpkNOj892uZ6OY5D+qvNnVcxcexS3tPEtXc66siESsgEAIAKAWdPavUpwwbZXAf5RmewdaqtrVdNHT3LXY9LXXz7Sta2TVYePIdK87SWqR6uXI4z3AAk72anxTk8Iizlpi32OMGlzi475VxGKisIo5yc5OTNtkGR5CsD0Lp7lA/qum5JfzXqfHkU8+pjctjUEAIDn6QD+jTcx3YtJ9LN6fWilauLwieNVb5l+uCMLocx0oZPG459SwMmrDhslZUR0UJs6ckvcP7uBub3nsHGQpNvTy8sh3VbStKPoLCsPjp4Y4IW6rIWtY0Dea0W61PKg20Bo4xiDKeJ8ryAGg7eFayeEbQjqeBJ7nGHPqZZcVqBnPrMpQfq09zrScrzboHGsQjhZOlWeXhckKWmzf6VLz39qgVeplnb+WhccxczszXmYt0aM7milXk6E/Vu5vNJ8IdZ/FVt/T460WFlUynBjfg8tn29IfioVN8TtdwzDIz4PLqyt/m8Hr2KwsZ6ay/JS3cdVP4GkL0RTkoAQAgIKAQ+6FPeWNnotJ6Sf0Xndsz+uMT0mw6f0Sl3EPFH5tHBmq+2jzZc1H6HDxWcNAb6Z/AZ/JWdpHMsv0K69liGO5hpmqwbKyJ042ZHkK0b4nXHAtzuYN1cMpwciN12/avU+DykUtWLU2NQct8nMlASgNHHPN5eY5az6Wb0+tFPzx+EeUqra4l6uRiLFgya1fO2JjpH5BovmtoRy8I0nJRiP/cn0adTwOqqhtqiss4g7Y4P7uPl+seM8SsoR0rBS1Z63kfVucyCgK10sldidbHh0RO5Cz6lzfqwA3tyvIDeS65L6pHfy4flljU8LWNaxgDWsa1rQNgaBYAdC6HApbTUf0qXnv7VX1eplzb9CF165Hc15CsmGjFRVgimY7evZ3NORWlaOuDRtReiaZYELtVwPAQqJcGXE1qixohfYtcN4tPVmpVOWJJlLUWU0ObTfPhXqYlA+B6WwBACAgrAKz02kvVvHotjb/tB+K8rtR5uH+MHrtjwxbJ92xMr3XeeIALWisRJs3xFnG360oHotHWcz8FbWq/l5Km7lmeOxmoZSLXzH4rrJnBRO5TEEZLk2dMM7FHissY1WOsFspySNJUoyfE7/c6xOWbEKhr3XDaeMgcZk/RTLdtrLK29io4SLMCkkIlAaON+Ql5jlrPkb0+tFTTt8I8qq2Xi5GMtWDY8aM4L9J1wY4XpaIh8182yTfUi4/SPEONTaFPHErLqrn6UXaFKIJKAW9OdIG0VM9/1iLNA2knYAN8k5LnN+iOtKOeL5IwdzzR59JAZKjOpqjus53wT4kQPA0ZctzvraKwjWctTyNS2NCj9N3f0qbnv7VW1eplzbdCFx71yJBpzSE7FjJnBpTNW6NWixcOm3SKN++5rb8ts1RVo6ajRcUpZgmNlI67GnhAXSL4ZKuosSaHWiddjDwtb2L1NF5ppnnqixNozrqaAgBAQUBV+mHnk3+n+W1eS2n9zL9f6PY7J+1j+/wDYnVZ8N3Ks0+lEmXMVq03mfy26lcUlimimrPNRmzTtWsmZijpQOI2LTJ1wb0VX6Q6ljIcBo7lTwcRqrezR/mKwtekp75Yki2VKIBKA0sa8hLzHLWfSb0+pFVTDwjyqrZeLkcvGql7WtjiGtNO5scTRtMjjYdGe1dKUNTOVxU0RLZ0N0ebQUscAzdm6V+++Z2b3HsHEArFLCKaTy8ndWTBjmlDQXHYBdYbwsmUsvCK6w2E4riTppBemw9w1QdklXYavK1gJPKQucOL1Heq9C0IsgLqRwKAofTyotVzAD+8ft5VWVn9TLu2X0IWHPJ2riScHhwQwYZFtEwx00WfemZxF46A42VTeLFRljavNMdcNP8JvT2rSL+kh11iox3w7yTOa3sXqbfyo/B5yr1v5NldzmCAEBBQFZaZttWSfzCM9Go0fBeT2osXL/R6/ZDzar9/7Eqq8d3KkOlEuXMVpR/EfzndquIdCKWp1s36cLWRvE3owFyZ1RmAHCsehsNPchA+kaq3s0X5isbTpKbaHUi31LK4lAaWNG0Ep/kctJ9LN6fWip6iqYC4k2tfaCqvOWX2Glk6fcvwc1Mz8SmHgs1o6QHgzEkvT4o6eFWFGGlFPc1dci0l3IwIBK070jjiaYg4axvfMbVFr1McETrWjl6mYu44/WoHHhqKo/wC9dqXSiPceYx6XQ4kFAfPWnlW3v2cashLZJAdWN5G3eIGagVKM5NtFtSuKcIJNnBY8H0hxOaQeorhOLi8Ml05xmso9LQ3MTwtkaMbNEfIHnv7GqrvessbToHrDfJN6e1c4ckRK7+tjzQi0bBwNb2L1NBYpx+DzdTrZsLsaAgBAQUBX3dAhtOx/ps/8T+q83tiGKil3R6fYc80pR7MQa4Weeg/gotF5gizn1CvUNtK/nH8c1cU39CKeqv5jNynWJGYm9GFzZ1RmawrHobDT3I22xGq/w0X5isbTpKbaHUi3lLK4lAaGO+bzcx3YtKnSzpS60UbpAxzhubDZ08kcYI3td4bf8VWUI6qheXM9NIvXCcPjp4YoIhZkLGMaP5WgAcpy2q2PPs3EBBWAK1Z3PMLmcXy0+u5xJJe+Qkk5n6ywoo33ku52sDwWno4txpoxHGC52qCT4Tsyc1saZydBACA86g4B1ICl+61E0VjiBmWQ36iq+56y2sX/ACxGKjE8xvWUascNFW2p28bpD+Nvgqu841MFhar6B7oGeAwcIb+P/wCrFNZaRX13xkx5jbYAcAAXq4rCSPOt5Pa2MAgBAQVgCn3Qaa8Ucg/u32PNcPmAqjbFLVSUl6FzsWrprOD9UVjijcweEWVNbvg0ejqc8ixiTLS39IA/BW9u8wKm6WKmTNTlbyNIm9GVzZ1RnYsehsNPclH9Y1X+Gi/MVjadJTbR6kW8pZXAgNDHfN5uY7sWlTpZ0pdaKWrspI5PVSxPPI14J7FV0p6Zl7cQ10sIvSGQOAc03DgCCN8HMK2ymeeaaeGZFkAgBACAEAIAQFMd1zzs8yLsKr7nrLaw6BDUYnmN62Rox+wWDViiZv6res5/FU1Z6qjZaQ+mmPeEw3kYOCx6GqXZ09VWKKO6niDfcbQvSFKSsgEAIAQGljFGJoZI/Sabc7aPxXC4pbym49ztb1d1VjPsymsQiOobixadnJkQvI0m4Twz3EmpRyhZxiLJrvRuDyG1uxW1tPi4lddQ4KRgpypMiJE6ERXJnZGzG5Y9DYaO5Kf6xqv8NF+YrG06Sm2j1It4KWVwIDQx3zebmOWlTpZ0pdaKcqPGdyqllzPSLkM+i2lJgaIn+EweLwtHADwcSmUbjTwZX3NnreqPMc6fSOmcL69uUH4KWq0GVztqi9DP9N0/rB1H5Lbex7mu4qdjFUaR0kY1nygDIXs7adm8m9iNxPGcHVC6HIlAQgOfPjVOwlrpACCQRY7R0LTWjdU5Pkipe6lURyVGvG8OBawZcQKg3Ek5cC1sk4wwxHKjkw9UtPuj2s9MgdG/+CxOSjFszCOqSRZmFRXeDvNz+AVNHjInXElGGB40dg8Z54mj4/BXmzaXOZ5y+nyidwK3K4lDIIAQAgIKArTTPDdynJA8Ccaw4Nb6w+PSvLbSobqrqXJnrNk3G9o6XziIdbT+Ow9HwKUZ8VIl1YZi0cWDLI7RkrN4fIqksPDN+Jy5s6pm1GVr6G419yT+0ar/AA0X5isbTpKbaPUi3gpZXAgNDH/N5uY7sWlTpZ0o9aKZqjZx5VSvmekXIxuOxEbHkSEHIne2FMs1weN2dmNY9ZWcsxhHPxSdxYAXHx498+kF2oPM0cLnhSeD6NVqefJWQQUBQ2ncrhVzWcfKSbCeFVlZvUy7tktCFl7idpJ5VxJOMGMlDB3NE6S7nSnY0FrecbXPQMulQ7uphae5KtYcdRYeEQENGWbzl2BRaUG8Jepzuqicvwh9oacRsa3gGfLvr1NCnu4KJ5qrPXNs2F2OYIAQAgBACA5Gk2Fd8QuaPHb4TD/MN7p2KHe22/pNevoTLG5dvVUvT1KixOnO22bLhw38svwXmKTcW4s9i2pJSQtYhDqu1xsdt5Vb288rSysuaemWpEwvXRo5ReTbY5aYOg2dypzY62oke4Na+CNoLja7g+5CnWs0lhlVtCnKTTRa4xCH1jetStSK7dy7E9/xesb1rOtDdy7Gni9VG+CVoe0kscNq0qTWlnSlCWtcCm6l3hnlKp3zPQrkYS7NDY8l3hIamNzrFZwDTr4i7VA9OM9AcCV1otKRxuIuVNpH0K3E4DmJW58atFOJQbuXYn6Sh9Y3rWdSMaJdiPpOD1jetNSGiXYo3Ty3fcpBBDnvItwEqtq8ZMurfoQtrkSMkRxl7g1uZcQBylYk0llhLLwiwsEw0NDYhsaLuPDvk9JVROW8nlk+TVKmP2AUes7XIyZk3nforbZ1DL1vkjz97W4aV6jEFdlYSgBACAEAIAQEFAImm2B6rjURjwXX3QcB9LkO+vP7TtHF76H7PQ7Jvcrcz/RXWIUlrg+K7YoVGrnlzLipBNNM4eoWO1T0cY4VaxkpLJVyi4PDNqNy1aNkzbhqHN8VxHIscjLwzYbiEvpu601Mxoj2Mn0jL6butY1MxoiejXyWzec+NZ1MzoiYGvWptk8B2azgZPEj80wYPErlkGNz8r74Qwz0MRlGWu7rW2WaOETE7EZvWO6ys6mY0RMZxGb1juspqY0RNWWQuN3G540M44GIlYMDNozhZH8Vw8J/iDgad/lKr7qtl6Ik2hT0rXIsLBsNJsxu05uPAFi3oOpLSiFd3KX1P9DvTwhjQ1uwL09OmoLSjz0pOTyzKFuaggBACAEAIAQAgMcsQcC1wuHAgg7CCtZRUlh8jMZOLyuZWelWjxgJsCYnnwT6B9EnsXmLy0dtPVHpZ6vZ99G4hpn1L/kScQod47fqlYoVscUSq1LUjli7TY5FWKaksor3FxeGZ2PWGZTMoctTJIcmAei9AAkyKxgHkPWwPL3oDy5+SGMmLXWTDMTigPBKGDySsg8koDsYFhJeRJIPBHitI8Y8PIotxX0rTEkUaOfqlyLDwnDjcZXc7YOAKJSpOctKNLq4SX4Q9YbQiJttrjtPw5F6W2t40o8OZ5uvWdWWfQ3FJOJKAEAIAQAgBACAEAIDDU07ZGljwC12RBWk4RnHTI2hOUJao8yudJtGTDcgF0W8frMPAfmvN3dlO2lrh0np7HaMa60T4SEmvoOHocFpRr9ibUop8zlPY5hs7r3ip8ZqS4EGdNxZ7a9ZwYTPQcsGSboCNZARrIYPDnLJk8koangoDy4oYZ5WUDzyb/BvlMjmd7B8BJIdKL32M/8At8lCrXX9MCTSoJfVMfMJwkkgAXdvcDQuFGhKpLCOVzdxivwPGGYc2IcLjtPwHEvRW1rGjH8nnq9xKq/wbwUsjkoAQAgBACAEAIAQAgBACA8PaCLHMHhWGk1hhNp5Qo47ogHXdT2zveN2w83g5FTXWy8vXR59i7tNrOK0VeXcQa/CHNJaWkEbWPy6lVbydOWJrDLyMoVY5i8o4U+Hlvi5fyu+BUyFwnzI87d/0mq67cnAjlUhST5HBxlHmAes4NchdDIEoDyShg8koDySsg8ErBh/g3KTCZZM7areF3wC41LiETrChOXMacH0fDLEDPLw37f8o3lCnWnUZ3W7pL8jpg2j7nZ21Wna5208gUq2sJ1OL4IrLraEVwXFjbR0bIhZgtwnfJ41e0qMaSxEpalWVR5kbK7HMEAIAQAgBACAEAIAQAgBACAEBCA08Qw2GcWlYHcB3xyHao9a3p1ViSO1K4qUnmDwKeKaFHMxODx6L8nDkcqirsqUXmlLh2Zc0NsLlVX7FOvwB7DZ7HN4ntuOgqBJVqXWmWdO4pVV9LTORPgfA0/5Df8ABdIXTMyoQfI0pMIcPrdDgQuyul6o5u2fozEcMl3rHpW/iIGnh5kfRcvF1p4iA3EzKzBZD9Zo5Llau5ibK3fqzdp9G77dY8g1R1lcpXb9EZ3EVzZ2aHR5rdjQDxDWcuTnUmZc6cBpw7Rd5sS3V45NvQ1SaOz6s+fBECttKEeCeRmocEijsT4bhvu3uQK2o2NOnxfFlTWvKlT8I6gU3gRCVkAgBACAEAIAQAgBACAEAIAQAgBACAEBFkB5ewEWIBHGtXFPmE2uRzanAKWTN0QB4WEt7Co1Sxoz5x/+Eqne14cpGhLojCfFe8ctj2qLLZNJ8m0So7VrLmkzWdoY3ekHSwfNcv4SvcdVteXtAaGj1g+5+qfwn+4fxeXtNiLRJg2yH/KAFvHZUPWRzltWb5I3YNHadu1pdznHsGS7w2dRjxxkjT2hWl6nSgpmMyYwN5AFMjShHkiNKpKXGTMtl0NCUAIAQAgBACAEAIAQAgBACAEAIAQAgBACAEAIAQAgBAQgCyAmywCLICVkAgBACAEAIAQAgBACAEAIAQAgBACAEAIAQAgBACAEAIAQAgBACAEAIAQAgBACAEAIAQAgBACAEB//2Q=="));
        pages.add(new Page("Đời Sống Pháp Luật", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAASkAAACqCAMAAADGFElyAAAAk1BMVEX///8QYpwAXpkPYpoAX5kAXJinxNl3ocEAYpwAXZcOYZz9/v/x9/rt8/gFZJ3O3uogbqNJhrGsydzl7/WJsMxvnsGdvtbb6PG3z+Etd6g8f6wAWJU7fKwdbKKSt9DH2ujU4+1Xj7cARovB1uZilrx/qchbkbgAU5OOs85Pi7UATI8zeqp5psZpmr9Ig68paqAAQosQmfoFAAAU20lEQVR4nO2cCYObNtPHZSHhQ1zmxpjD5ViIDX2//6d7R0ICe5Ptk00b77bLP01qc1n8PBqNZoQRWrVq1apVq1atWrVq1apVq1atWrVq1apVq1atWrVq1apVq1atWrVq1apVq1atWrVq1ap/hbxm+wvKP7rZH6AyI7t3C18/utkfoMIimqZtxB9tIyReaZrYLDZNB2ymbXwjyT662R+gwuIQcPqSUkCQvuyBRJpq5CUFJviFalS8oi97kr5wpXA4cT662R8gTkpLrboJnPSwGRqHbqwhIM6tJ9o+ac7p2PR0Q8cm0YYb17AhX5dUOpbwKqpftAi56f7P0sQjitNNOiD/jxx5Dk07VP/piRMigrWvSmqfeCxvG9M4vkSIXV4y26Mjc1MNSDVACoXpi4/aPz2vruvhQg5f1aYOaYjqby/fzkaVlqZpY8329mcgtZlIsci4fOs4Kfv//u+PP759XT+FtbJIqaV9c82sLAO2TaVNKVJBVEw2ZeZdnjfky/qpfebFMKBZL7nhlN4u9rLCo+e73tfWqL1xUogxhiL8Zf0UwYVnvWzStIis0jucDR1s6gSkiCQVfKuinJOKxtN5PPO46muSOqQ3pFsvtEPhS+RlLzkyuE3p315eJKk/RsNAF/BT38CdvUB09TVtKtsQHKNSt5GdUU7K8hD3U54bt60gtX15CRG3KcN13Tgn5Kva1GZDd75neGH2crDLP3EaGCU5e6bBut5ovvnG8EKd0mj/LA0Qs/H+y/opbbNP/0yyFCYyTsJjgCQhVgLKrHOGsyQ7bLCTWJskcfifzZeNEgif+9KUAjFw6zDtw3zel4IwSWGcg7/agab7DeXbUqptDl+V1JvJFe3NrMtXJGW4+i+o+uhmr1q16iuImRAdmZ7JProhHyXGDBMEIeJfH+cNp3Ecr23xnGb9PVXh3WgC84S4skvv4Q4jvh1kvDqTFXElFZfLZqN0u6AdxxNA6NtbHkevT0QiHQDyekpwmjXRv8GkWE2J0hSmWFnmnOq8nA8x9BOlEL603uOp3pVYUniYN+ZjtsN7jDEhmP/Bu2zsHs+MmvbS5waKEkpoFkS/9Qb/MbGBaqowtNkcRAEJ7nFPM38xBcM9wk2no/1wqpcoyDht5baCbxNlJw22i6ITzH1psnQvFvlOSiHSPlYRWLD7r+h4XCzAEhTc3m4nZhOiqEbo9q5PGPHtdgsei7aGnkuFeTxt8s5UFOXgn921PqlqnUZ7U51VXa7jse8v/bH/d0WOnJRSFrp5QtQ7YkkTMu0qzPPO9/Ncr0phaeWtuXVmNfkvkC4tw91tZGGTtCYyasl9s7HK6QDP1jsfoDd+7la2tFqjWmQgr9D9pvHDWPVK5hWxG+pxwftwVCq98gW/XXekiAMfru8Oqoa7c/ldxNuTY2EqhInljEFsoiqlaVKcUyqV1tPVciJJabiDt83sAzG3H9MNzo61w5OsLDk2MYdVWLMytztl06dZSVDyaxZBklnQ+a3sHBRF4ijdnk1qmElpDnyLlbWZSYUI2b2FueMBt0PpgSdDCLWOYWdpOLFHosrhZHhFakMu0N86J5nkOECqOlnSie15ZoBfi1iXQmQ8RRGdbA6Zk6ZEuDnwbzSBBsXZXjm8fWo58/BDg2eTCrBaCUAeSG04qSKjktuuzwe560DAuDDto9PSU2Wz47n3bfApjCCo8qQYii0sXaBW561ca3CglivyeFL7s984c5+lPjKvi80n9VFbrr99MihjwOqzNd774jtSOrooUJusROYoGw13mRyDyDx/R8qEE9QKDLxL2tCeB1A4W34lJPFg3JTnaiQx7Ts7BtsL5hbRAdnWzMaKmdGTjyIVHhPtoO4383Xj3qb0KFMsiGMi1lK1C7sQOnrf25SIJZWj43GCdfKLadirLCLtiIwGMk9YLmABOOXymU4J3m2+LG5RtVNoDtzkt7OFPZvULSXasg4npdFdT9i5yxuSwVA2W5iWhpzKeekYs9MwwqOFsbga/+9AUoBlQ7yhq/t/JAW3HC+kCPdM/mI3F1Qso6kg9WE2pfeLV9homRM92NRC6iBIKTIazd8iBayK5gyue8LA/xKcBR4K55PxiZOaDYd+T2q2mwvYlCI12dRi1k8mBV6BKlIki0tWWXNn/CtSb9rUBKtqjhnF05XEuNkzXd3ja1J/bVOvSX1Y74OxbyH1nUd/g5S2eYsUMyYx5tn50VJeWwMc7jts6i9IfZxN3UWeoik/Q2qzectPlTBHOXL1R58Z8RUvXkXHcoT/W6SCDyDF7LzJy/vI8+dJvenRqx3PHvBEAj1CfBDO90yO4eTi/2bv+wBSrLGwY0HYt3z2K1LaL9hUYanVq/hioLuQkiSN9U+Qqp/vp2K8yz37mBgLKZJBU3Tl0LUfRAlz5In1t0ipeR/hpCJniceGbD75gdThPp76ASntgVRLnx0lsIFyLvkhWnzkhjclX95qb5ECDvGPSZXOcpNAyrjOlpNdFlIinlI4DlbxJilyQfbiDTIbGePTbYpdMExWjD4xF1J87DNm8+Y2VWZqn+h97Tz27Yofk4IJjooTe4g2zTmHozk3Z04zPNpUVr4ZT9ELg0sobjD1rJd5H30WqYDio9G9+Hd+SrOC7WX5CgGHmSgWvGcax3muxu0RSH03m2FBKg1nk3VVFRxmE0uKiRpPGSSmYDglWXFi2NK78UkgJ7WMmBcGESs9TFeEsCwl2rzvWVmXGFqaHFoDDVSbc54wZmnTQwQ8ZhpNlOPp2QNNO7T6zVItJj66JwU3ryJPnhpXmRhrhzdT/mSDDzmEQlPaeKMdAj3YyWwosfQp6zJdR5HSZlIovjgWDKg8Q+UM7WxT5FmkDDB/3BjcYc1GpDoO4SO91ZdwlG/hKQnFk+tihqhhmoTsgZSY9CtUgSN4yyc3+N1jao06Q+b2sJ9MAjaJi/Jk19mV48BEdyYl3gpSiEWVHuZ5qFcR0nfqI3HzHFCeDwE0aUTkaT0qy5zkdPErkS9h1eAQkYPcq6znsZNpWy/Bophz2O1oPV+ZlfpwynhcRXlctdslvV+JdAKLa7gWFteCq8HO7BjyHG8hCjlclA8qTYrFuRinRxYNQbBtmuZ2uwFt1KieqVH/KaCKJD2Bq7RgCDM8L3qU5xnsrthgFnrebINhCLY3nt2ed7HSFiqqqiofLm9ERTyZgVuVd/VhJq8F2t46vfKmXV44S4fvx875C7H6JUamM6egyaUZJo/GH6FJHj/yN8lO0rOnE57/YLr/y3Wkgt9bnv+Wep2n513HW1Y5lEyZIfCPWCYniDWUv+NTXyvq6c6FEAFrNM+p84ukzM5KaXpoq+9LxH9XrLw5fOXdrrYZirrRsTTpAfYYa5YzNsVzqs6hha8en6RtNjCl+UVQxi05X0/+bzGoom6hs4PqqcDsQWfOfV7b8nNZzXqOOkzO3Mduof9lscqUyHSJPOauVMDUS+Fx1PuoABdVGqb0NeCb7j2deb+ywet8qVBV+NQlPePuo7xpr6eHSyFRd02ezGF3mtv6+0mFFuG+HGbDB5gGx8lJaezrW1fxr4xdsqmqBlMI25leZ7IQ7MLwCAOkqFA5WaaKotmdnGQc/Llb2pYYNmEsu5qqBfJAC0awrXqTTZXocHc/FPOqo3sVrRv5shCl8/kJJeiy35Ns6w8J4UZVDelcv+TeYJf4BsTjlBxAeGejYkf4a4LHabBvqEb4H3LA5EBEUo+TIip+PQjfu6e7o7wX25Hx62TKHIYqmfKxPqCaXEEykbpNEd4UjR72jTgcywbO9T5M4t9PCppOCaUpbmuq0b5I8EZG4lPkSQMD3P3UUMueJ6nkOJFq5+ne/fzLnWNCGTduDjSzFSkZLZ4UKXWJvb/kMQ/C0BHryf31CcwjVJ5rrrfxy++eQQqVdeIkvW54JyuzuljkSjS8x3JxConZ26S86wMpfDRekyJYTs9kQPo+Ul7ySCpR1QrtjhSfBj2FFMwQShFCRkVpl1OFTav9yzQB03DzF6Tu8iBiKy/R3ZPCfazWgZDJL72PVJFpDx9gFSg8CJclbf4wvXsSqUUxzBRa3gjLRWY7TRbwoEhtgFTxSGpa46Geyeel3UdSfMGAPtUzJcX3kXJlV7MyaaEd8mJQNVe7ya2Iqzh+8mIXNsBsYc/vHCb1KuGBW+OBlHZPqqHTDTij7GL+a1KMr27QBCn7/aQa+Ta5yJPUnLKbSenPZTRpqmKJZWaClLjDR1KPvY9dJKmjPw0B+1Y4qoXUwNQ6kMMU1r6LlNFScVk8htb0QSq08KlMT5DwI0jxTN6URJI2JWzhjlQ1L1WYSHlTxlcjg2TDXS763qbEruz9NuUlB2lKhbiitpEj6Jzh+0BS0+fzUXf7nU1p1/Gq3fc+5XD3vrI1SxjOQmrL6xZTGeYXSCmviBtPZg6x7GwzKfyhpMCm8kh3DpPl1wspCPI29zYlV2JoVJeVFw2L5Z/3Y59+ms7+FY8eThcFZ2Qk09BBZc7uk5AS6zd2cuHLEiUsy4oVKeVwiW3KfohFangmpWk7IuMpfH5/lHAjkyVZ1dQGbSqIfR5S4CgPc0lgF7PjXXh5TwocugwAPBlMwxzFQI8xulw+rNEOvZeUitC5OcqmHZLoM5HayNQ3X15p+WwmtQTcE6nZkCA0l6eSrEQPNsXvDjptagXGu0mpCJ3AodNgB9urz0SKD74i720lATRs8VNjO8q7EqRKGSnzGkM3xYiaWGR8Z1Migk76Od32HlKFjDd5FKUuKcl8ElLQgFvsunExPeViHN+IPF06DeI83nSt5fWdR6/Lsow8c0mF/oCUGiW+I6WKL/utabrZNA0gtUD+SUhpj3PzNyPPBstC1LjdXrS7stxCKnidYnuPTTXKlp3zNZFjiUzWfB5S93OEB1K7hRS7zOta91jdLuax50Jq+N+k9LdsCq4v9/AclBxLyJQt/CSkNiJGn3Wfn7qfIcsIXf1gnTq1ejWbedQdKdklZ1L4jhSfayuHrqkkl9BOoPkkpLTJLyu9lckr1JoOnsVaVu+EP0eKXG3+xEuEYvXMyZTzlBcE4MW8RAETuiyxEpX1z0Jq81Ok5sdqNs7pfFKZKu6afoYUeB+ejz8uq973N8RaxRHmPi5Wnft8uwWOrM5P2cLPQurnbKqZfVNnGJ6ctAj/83OkCF/fcDLmZzxIEnYzNeDRUGlTPD0PDKfMoliK/hlIyYf7Hv2UfFaI+6lpAaEGpNi8vIpzZfMivSwCUtKx/GjsW7KYfN0GeCt3p8ZQjDdTUpCne41WuUHCrx9Im9JE7OmrGPnDSE2t+Y7UVIIQpA7TapfRhAhdTgR52+FO5CIVuC/+aJ/QD8a+DM8rjzRywHz2oztU1tBl7p5mrojQNTkH4MOdL4M3kvJJ+JQS4smFDyHF6hTLAtYDqSOdFgdBi4tpJQqhJ9NWD+dNZZeGql0BhKSywkTr16TKq/Ognnud8pbsyFKTcgJ+QTtTZT4nsmNPX55z5utcUtEgMnF7vvTtTer+KWOm+53fdZ3ve8jzxavO11nUyedpO1ENtjv53ndRNK9XqV6TYq8X08glLnHTjtckSU59EJZim1nMCjIryW35puK742C7DcTff9djuf+ImGG+qswr2RAj0OSfXxzy31PkUErr/x4p45+/JXvo/Wc/lP375fmN/b+PWgUmZY8/KMExNze/34oi/+ccMtPDT9f5lEP1psfu0fcOlqmtbN778L/4R8tTTf/2I0tzb53Bf9nl1Yc/NIb/4zX8dPZGa36w+bfLDsahYCjuh1gsSSqDY10hE4biqC75jTEInTtmbGN4c+zrwUf2lkUtn2DYw3GwxSGhD03vcoHUz+FUmAa3rc7YjU9L/HC6UNgx+D4iiFn7nqeOWXwZtyXS4bURiNmUXXv88LAzg8vllBf9hf9UR9yXECQYcB7YbtHr/OcJnl5ILpzRvyRlbA3bI9wHTD2O/sVyjURHZVYg44xtZGaWbVzFzyeQpkuMfOe5iclTucemP0f5maGgZyiyHLFKrR9QaUVxNmyzkI03vmULGK8MbVuG+IMVKEwc6Fsst2q/Hz24JKpSMQ02nRDGvWrbes7Q+XE0UP51hS81nGKiij/DHFP4p3l50nr0WexygRZ75gjNj6/wbbc1X0yWRCcdWlwgt28CZCZJbYx8DhE7pufYAa5uAkDN1zKyfJQMhppHz6y+lLpT8OPdxLvwRe8tHJ0fGWpqZvT5CDy3fK1/lHGD85APM8GguQj/1VyQe2bbwUv0qPTgEACFQiep4qsJB/UxirOrbRyT56xHX2RehRWbCfgEI4E56nhufaN04tEVpNrezzzD0fuwn0h5rL7Vwe0U8+9/Sj9wBtsLM85BcOVdKtg5mRPz2pN3LVtBqlGkkJuFYDYs4HOeyjLd8dKGYJSRkx/FgxJlEg05CgbvajlZjWzx6zLhERpwMiLHv7TwhQZ5cayfblNtbyIvLI43/sMgJV/RUfd1N0bXHG6kjDInOYRGFldJogtSEQp3ge1wDsIevTCCfsH4HViOw38ZAmwqcsGmOpgqKZvipBKD1QOrrSSD3iNIgU1Vtzr1wxPLd4kjKs5G3ZxKTirRzciEmaAgdTLr82iEcJDlxdf4MnS3Z5MCv3TeXvnT/+1gca9cXvtglyZRYwXOaECrveYaZToaXjgp14pQSX0ja6dzT9trEpXZsSUu9MUoanlfvoCfyqLYqgNL+qk2GWJ+FI7tXWwWlouGljv/kNTNeDS7s+f4XnkVCyFjGjA21J7T+tsQ2SKpAJRtfPKSxivPTWxFoxXVT/8VABTdxgYcR9H2uhh5o+a41Wub5cfGMwJerBwKMRByN1IEHjJuBfKnflduj+Lc+hIjr4Zv325h6Opg7IMBrjrzEkIDgFneg9eya3Ay+s2wTRgc+SgIquojROJ6U9a8aLEVPp1PgPPc3DrW0ReDKNC7GahrioE//nurAiPcsu4jFlFNocnyrAy8Yuw+iBI/a8fmMIbdRTPLuXK/fCUs5qQ2L1eAKAG8G5s/7W73fDG52ReDIbs/SrZGtfA/I/27TBWXefnJpEkYfLoYfdWqVatWrVq1atWqVatWrVq1atWqVatWrVq1atWqVatWrVq1atWqVatWrVq1atWqVav+U/p/q1jb18fZt/sAAAAASUVORK5CYII="));

        pagerAdapter = new PageAdapter(getContext(), pages);

        rscv.setAdapter(pagerAdapter);

        rscv.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }
}
